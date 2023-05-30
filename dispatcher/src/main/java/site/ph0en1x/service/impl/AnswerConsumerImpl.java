package site.ph0en1x.service.impl;

import lombok.extern.log4j.Log4j;
import site.ph0en1x.controller.UpdateProcessor;
import site.ph0en1x.service.AnswerConsumer;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static site.ph0en1x.model.RabbitQueue.ANSWER_MESSAGE;

@Service
@Log4j
public class AnswerConsumerImpl implements AnswerConsumer {
    private final UpdateProcessor updateProcessor;

    public AnswerConsumerImpl(UpdateProcessor updateProcessor) {
        this.updateProcessor = updateProcessor;
    }

    @Override
    @RabbitListener(queues = ANSWER_MESSAGE)
    public void consume(SendMessage sendMessage) {
        updateProcessor.setView(sendMessage);
    }
}
