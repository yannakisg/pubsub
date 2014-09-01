java -server -Xms256m -Xmx1024m -XX:PermSize=64M -XX:MaxPermSize=1000M -cp ./bin:../PubSub-api/bin:../PubSub-network/bin:../libraries/*:./conf pubsub.node.start.PubSubNodeStarter $@
