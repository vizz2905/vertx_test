
From vertx google group: https://groups.google.com/forum/#!topic/vertx/k4f7EWTnIuU

<<<<<<<<<<<<<<<
Hello everyone

I'm facing a problem where I'm trying to load test the event bus. We wanted to replicate web clients requesting for data but unfortunately had to revert to using a verticle to simulate clients. Now the problem is that we seem to overload the bus somehow, we get an exception where we get no "pong" and considers the bus dead. We are unsure why as there doesn't seem to be that much load.

https://drive.google.com/file/d/0B2ICwc83bqdjYXJfSkhyeTBnUms/edit?usp=sharing
The following project contains a simple test where we are looking to stream data via the bus. We have 3 verticles:
- MarketMapVerticle.java: this is the stub verticle for pushing data over the bus. Each symbols requested by the client will trigger to send 20 updates every second on bus named after the symbol. (i.e. if a user requests for IBM, it will use a specific bus name containing IBM)
- VerticleInfoDump.java: this verticle simply dumps statistics as to the number of client verticles, messages per second and average speed.
- MarketMapClientBusOnly: this verticle is the client emulator. It will request 100 specific symbols for streaming data. The symbols used in this test are unique.

To test the project you need to do:
- edit the set_jre7.bat file: this file sets the paths for java and vertx if they are not already set for your system. Comment the lines out if both are accessible already on your command prompt.
- run the "start_all_test" batch file. This will start, in this order, the MarketMapVerticle, VerticleInfoDump and 5 sets of 100 instances of the MarketMapClientBusOnly.

Now the problems I'm facing, with this actual test, might be related to my environment but we tested this on different machines and we hit this issue by increasing the number of instances. We tried with 5000 instances of the client test verticle.
>>>>>>>>>>>>>>>