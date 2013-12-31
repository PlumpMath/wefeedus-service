# About

This is a prototype of a service to allow people to eat with people
nearby, steared by a map-view. It is designed to be integrated in the
flow of social-media apps as well as being used as an independant
web-site or in other contexts for different purposes (read: it is very
modular due to a functional design with Clojure and
[Pedestal](http://pedestal.io) in particular). The prototype is still
simple and not yet ready for any end-users.

WeFeedUs is the bootstrapping application of a
[general open-source and open-data community](https://github.com/functional-nomads)
around open web-development. We are trying to build a community around
open-source to finally open up data (after all, data is more important
than the code that runs on it) and allow to cooperatively compute on it
with distributed applications like this. The general purpose is to make
people literate in programming by allowing them to tap their own
data. While this service currently uses [Datomic](http://datomic.com) in
its free-as-in-beer version, work is done towards a free software,
distributed version control system for data on the web with
[geschichte](http://github.com/ghubber/geschichte). Due to the
"high-level", functional design of Datomic, it should be fairly easy to
replace it once the application data stabilizes.

Application data will be released under an open license and development
will be aligned around strong copyleft products. If you are interested,
ping me.

# wefeedus-service

The client-side (browser) part can be found [here](http://github.com/ghubber/wefeedus-client).

## Usage

Run  `lein run` to start the websocket service.

The service wires web-sockets to Datomic atm., but doesn't yet do much
except storing markers.

## License

Copyright © 2013 Christian Weilbach

Distributed under the AGPL version 3.0.
