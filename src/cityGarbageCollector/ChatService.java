package cityGarbageCollector;

import cityGarbageCollector.agent.CollectorBDI;
import jadex.bridge.IInputConnection;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IOutputConnection;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.types.chat.IChatService;
import jadex.commons.future.IFuture;
import jadex.commons.future.ITerminableFuture;
import jadex.commons.future.ITerminableIntermediateFuture;


/**
 *  Chat service implementation.
 */
@Service
public class ChatService implements IChatService {

	/** The agent. */
	@ServiceComponent
	protected CollectorBDI agent;


	@Override
	public IFuture<String> getNickName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IFuture<byte[]> getImage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IFuture<String> getStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	//valor booleano reutilizado para identificar se se trata de uma msg inicial
	@Override
	public IFuture<Void> message(String nick, String text, boolean first) {
		System.out.println(agent.getLocalName()+" from: "+nick+" message: "+text);
		agent.receiveMessage(nick, text, first);
		return null;
	}

	@Override
	public IFuture<Void> status(String nick, String status, byte[] image) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ITerminableIntermediateFuture<Long> sendFile(String nick, String filename, long size, String id, IInputConnection con) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ITerminableFuture<IOutputConnection> startUpload(String nick, String filename, long size, String id) {
		// TODO Auto-generated method stub
		return null;
	}

}
