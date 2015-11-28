#run compilation
cd java-cgdk
./compile-java.sh
cd .. 
 
#run local runner
cd lr
./local-runner.sh
cd ..

#wait for lr
sleep 5

#run client
cd java-cgdk/classes
java Runner

cd ../..
