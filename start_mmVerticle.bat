call set_jre7.bat 
call vertx run src/com/sungard/marketmap/common/vertx/verticles/MarketMapVerticle.java -cp ./bin -cluster  -conf verticle_conflated_DJ.conf -instances 1