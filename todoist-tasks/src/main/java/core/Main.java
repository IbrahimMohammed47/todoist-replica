package core;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;


import context.MServiceCtx;
import io.github.cdimascio.dotenv.Dotenv;
import util.CommandMapper;
import util.ControllerNettySocket;
import util.QueueConsumer;
import util.ThreadPool;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutorService;

public class Main {


	public static void main(String[] args) throws Exception {

		Dotenv dotenv = Dotenv.load();        
        
        
        int delay = 5; //seconds
        int max_retries = 20; //seconds
        MServiceCtx ctx = null;
        ExecutorService pool= null;
        CommandMapper commandMapper= null;
//        for (int retries = 0;; retries++) {
//            try {
//            	ctx = MServiceCtx.getInstance(); // sqlDB + cache + Qproducer               
//                pool = ThreadPool.getThreadPool();
//                commandMapper = CommandMapper.getInstance();
//                break;
//            } catch (Exception e) {
//                if (retries < max_retries) {
//                	System.out.println("ERROR");
//                	e.printStackTrace();
//                	System.out.println("RETRY "+ retries);
//                	TimeUnit.SECONDS.sleep(delay);
//                    continue;
//                } else {
//                	break;
//                }
//            }
//        }
    	ctx = MServiceCtx.getInstance(); // sqlDB + cache + Qproducer               
        pool = ThreadPool.getThreadPool();
        commandMapper = CommandMapper.getInstance();
		QueueConsumer.consumeIncomingMesseges(dotenv, ctx, pool, commandMapper);
		ControllerNettySocket.startControllerNettySocket(dotenv);
		
		
	}	
}
