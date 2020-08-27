package com.zq.consumer;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.event.ConsumerStoppedEvent;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class KafkaConsumer implements ApplicationListener<ConsumerStoppedEvent> {
    private static Logger log = LoggerFactory.getLogger(KafkaConsumer.class);

    private ThreadPoolExecutor executors;

    private volatile boolean isClosePoolExecutor = false;

    @PostConstruct
    public void init(){
        executors = new ThreadPoolExecutor(6, 6, 0, TimeUnit.DAYS,
                new LinkedBlockingQueue<>());
    }

    @KafkaListener(topics = "overtime", containerFactory = "kafkaListenerContainerFactory")
    public void onMessage(List<String> msgList, Acknowledgment ack){
        CountDownLatch countDownLatch = new CountDownLatch(msgList.size());
        for (String message : msgList) {
            submitMsg(message, countDownLatch);
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            log.error("countDownLatch ex: ", e);
        }
        //提交offset
        ack.acknowledge();
        log.info("commit offset!");
    }

    private void submitMsg(String message, CountDownLatch countDownLatch) {
        executors.submit(()->{
            try {
                dealMsg(message);
            }catch (Exception e){
                log.error("dealMsg: ", e);
            }finally {
                countDownLatch.countDown();
            }
        });
    }

    private void dealMsg(String message) {
        log.info("dealMsg: [{}]", message);
    }

    @Override
    public void onApplicationEvent(ConsumerStoppedEvent event) {
        isClosePoolExecutor = true;
        closeConsumeExecutorService();
    }

    private void closeConsumeExecutorService() {
        if (!executors.isShutdown()) {
            executors.shutdown();
            try {
                // Wait a while for existing tasks to terminate.
                if (!executors.awaitTermination(120, TimeUnit.SECONDS)) {
                    executors.shutdownNow();
                    // Wait a while for tasks to respond to being cancelled.
                    if (!executors.awaitTermination(120, TimeUnit.SECONDS)) {
                        log.warn(String.format("%s didn't terminate!", executors));
                    }
                }
            } catch (InterruptedException ie) {
                // (Re-)Cancel if current thread also interrupted.
                executors.shutdownNow();
                // Preserve interrupt status.
                Thread.currentThread().interrupt();
            }
        }
    }

    @PreDestroy
    private void destroy(){
        if (!isClosePoolExecutor){
            closeConsumeExecutorService();
        }
    }
}
