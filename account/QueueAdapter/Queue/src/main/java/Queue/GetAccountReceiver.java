package Queue;

import ApplicationPorts.AccountServiceUseCase;
import DomainModel.Account;
import Model.AccountDto;
import Model.ViewAccountConverter;
import com.rabbitmq.client.*;
import exceptions.RepositoryConverterException;
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
public class GetAccountReceiver {

    @Inject
    private AccountServiceUseCase accountServiceUseCase;

    public void init(@Observes @Initialized(ApplicationScoped.class) Object init) {

        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(Consts.HOST);
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.queueDeclare(Consts.GET_ACCOUNT_QUEUE, false, false, false, null);
            channel.basicConsume(Consts.GET_ACCOUNT_QUEUE, new DefaultConsumer(channel) {

                @Override
                public void handleDelivery(String s, Envelope envelope, AMQP.BasicProperties basicProperties, byte[] bytes) {
                    Jsonb jsonb = JsonbBuilder.create();
                    String message = new String(bytes, StandardCharsets.UTF_8);

                    String id = jsonb.fromJson(message, String.class);
                    System.out.println("[ RECEIVE ] ACCOUNT - get account - " + id);

                    Sender sender = new Sender(Consts.GET_ACCOUNT_QUEUE);
                    try {
                        Account account = accountServiceUseCase.getAccount(UUID.fromString(id));
                        ViewAccountConverter viewAccountConverter = new ViewAccountConverter();
                        AccountDto accountDto = viewAccountConverter.convertTo(account);
                        sender.send(jsonb.toJson(accountDto), id);
                    } catch (RepositoryConverterException e) {
                        sender.send(Boolean.toString(false), id);
                    }

                }
            });
        } catch (TimeoutException | IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
