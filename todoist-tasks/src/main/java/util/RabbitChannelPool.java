package util;

import java.io.IOException;
import java.util.Vector;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

public class RabbitChannelPool{

	private Connection con;
	private Vector<Channel> _free;
	private volatile int debt = 0;
	
	public RabbitChannelPool(Connection con, int poolSize) throws IOException {
		this.con = con;
		_free = new Vector<Channel>(poolSize);
		for (int i = 0; i < poolSize; i++) {
			_free.add(con.createChannel());
		}
	}
	
	public synchronized Channel getInstance() throws Exception {
		if (_free.size()>0) {
			return _free.remove(0);
		}else {
			debt++;
			return con.createChannel(); // allows borrowing more than poolSize channels
		}
	}

	public synchronized void releaseChannel(Channel ch) throws Exception {
		if (debt<=0) {
			_free.add(ch);			
		}
		else {
			System.out.println(debt);
			debt--;
			ch.close();
		}
	}


}
