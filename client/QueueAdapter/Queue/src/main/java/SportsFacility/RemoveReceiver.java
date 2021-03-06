package Queue;

import ApplicationPorts.User.SportFacilityServiceUseCase;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import exceptions.RepositoryException;
import utils.Consts;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

@Singleton
@Startup
public class RemoveReceiver {

    @Inject
    private SportFacilityServiceUseCase sportFacilityServiceUseCase;

    public void init(@Observes @Initialized(ApplicationScoped.class) Object init) {

//        try {
//            ConnectionFactory factory = new ConnectionFactory();
//            factory.setHost(Consts.HOST);
//            Connection connection = factory.newConnection();
//            Channel channel = connection.createChannel();
//
//            channel.queueDeclare(Consts.REMOVE_FACILITY_QUEUE, false, false, false, null);
//            channel.basicConsume(Consts.REMOVE_FACILITY_QUEUE, new DefaultConsumer(channel) {
//
//                @Override
//                public void handleDelivery(String s, Envelope envelope, AMQP.BasicProperties basicProperties, byte[] bytes) {
//                    Jsonb jsonb = JsonbBuilder.create();
//                    String message = new String(bytes, StandardCharsets.UTF_8);
//
//                    String id = jsonb.fromJson(message, String.class);
//                    System.out.println("[ RECEIVE ] CLIENT - remove facility - " + id);
//
//                    Sender sender = new Sender(Consts.REMOVE_FACILITY_QUEUE);
//
//                    try {
//                        sportFacilityServiceUseCase.removeSportsFacility(UUID.fromString(id));
//                        sender.send(Boolean.toString(true), id);
//                    } catch (RepositoryException e) {
//                        sender.send(Boolean.toString(false), id);
//                    }
//
//                }
//            });
//        } catch (TimeoutException | IOException e) {
//            System.out.println(e.getMessage());
//        }

        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(Consts.HOST);
            final Connection connection = factory.newConnection();
            final Channel channel = connection.createChannel();

            channel.queueDeclare(Consts.REMOVE_FACILITY_QUEUE, true, false, false, null);

            channel.basicQos(1);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                Jsonb jsonb = JsonbBuilder.create();
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);

                String id = jsonb.fromJson(message, String.class);
                System.out.println("[ RECEIVE ] CLIENT - remove facility - " + id);

                try {
                    sportFacilityServiceUseCase.removeSportsFacility(UUID.fromString(id));
                    System.out.println("[ SUCCESS ] CLIENT - success");
                } catch (RepositoryException e) {
                    System.out.println("[ ERROR ] CLIENT - failed to remove facility");
                } finally {
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                }
            };
            channel.basicConsume(Consts.REMOVE_FACILITY_QUEUE, false, deliverCallback, consumerTag -> {
            });
        } catch (TimeoutException | IOException e) {
            System.out.println("[ ERROR ] CLIENT - internal problem");
        }
    }
}
