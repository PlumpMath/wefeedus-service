(ns wefeedus-service.core
  (:use [lamina.core :refer [enqueue channel receive-all]]
        [aleph.http :refer [start-http-server]])
  (:require [datomic.api :as d :refer (q)]))


(def uri "datomic:mem://wefeedus")
(def schema-tx (read-string (slurp "resources/wefeedus/schema.edn")))
(def data-tx (read-string (slurp "resources/wefeedus/seed-data.edn")))


(defn init-db []
  (when (d/create-database uri)
    (let [conn (d/connect uri)]
      @(d/transact conn schema-tx)
      @(d/transact conn data-tx))))

(defn get-all-markers []
  (let [conn (d/connect uri)]
    (q '[:find ?type ?lon ?lat ?start ?end ?date
         :where
         [?e :wefeedus/lon ?lon]
         [?e :wefeedus/lat ?lat]
         [?e :wefeedus/starting-minute ?start]
         [?e :wefeedus/end-minute ?end]
         [?e :wefeedus/date ?date]
         [?e :wefeedus/type ?type]] (d/db conn))))


(defn db->marker [dbmarker]
  (let [[type lon lat start end date] dbmarker]
    {; HACK:
     :type (->> type
                (d/ident (d/db (d/connect uri)))
                name
                keyword)
     :lon lon
     :lat lat
     :start start
     :end end
     :date date}))


(defn add-marker [{:keys [type name lon lat start end date] :as marker}]
  (try
    (let [trans [{:db/id (d/tempid :db.part/user)
                  :wefeedus/lon lon
                  :wefeedus/lat lat
                  :wefeedus/starting-minute start
                  :wefeedus/end-minute end
                  :wefeedus/date date
                  :wefeedus/type (keyword "wefeedus.type" (clojure.core/name type))}]]
      (println "trans: " trans)
      (d/transact (d/connect uri) trans))
    (catch Exception e
      (println e))))

#_(get-all-markers)

(defn dispatch [msg-str]
  (let [msg (read-string msg-str)]
    (case (:io.pedestal.app.messages/type msg)
      :add-marker (add-marker (:value msg))
      (println "no dispatch value for" msg))))

(defn start-server!
  "Starts a listening server applying dispatch-fn
   to all messages on each connection channel.
   Returns server."
  []
  (start-http-server
   (fn [ch handshake]
     (init-db)
     (receive-all ch dispatch)
     (enqueue ch (str {:io.pedestal.app.messages/type :load-markers
                       :io.pedestal.app.messages/topic [:markers]
                       :markers (map db->marker (get-all-markers))})))
   {:port 9123
    :websocket true}))

(def server (start-server!))

#_(server)
