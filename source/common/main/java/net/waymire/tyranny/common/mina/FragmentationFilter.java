package net.waymire.tyranny.common.mina;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.waymire.tyranny.common.logging.LogHelper;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.filterchain.IoFilterChain;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;

public class FragmentationFilter extends IoFilterAdapter {
	private static final String CLASSNAME = FragmentationFilter.class.getName();
	private static final Logger LOGGER = Logger.getLogger(CLASSNAME);
	private static final AttributeKey MESSAGE_BUFFER = new AttributeKey(FragmentationFilter.class, "messageBuffer");
	private static final int DEFAULT_BUFFER_SIZE = 1024 * 16;
	
	@Override
	public void onPreAdd(IoFilterChain parent, String name,NextFilter nextFilter) throws Exception {
		if (parent.contains(this)) 
		{
			throw new IllegalArgumentException("You can't add the same filter instance more than once. Create another instance and add it.");
		}
	}

	@Override
	public void sessionCreated(NextFilter nextFilter, IoSession session) throws Exception 
	{
		IoBuffer buff = IoBuffer.allocate(DEFAULT_BUFFER_SIZE);
		buff.setAutoExpand(true);
		session.setAttribute(MESSAGE_BUFFER,buff);
		nextFilter.sessionCreated(session);
	}

	@Override
	public void sessionClosed(NextFilter nextFilter,IoSession session)
	{
		session.removeAttribute(MESSAGE_BUFFER);
		nextFilter.sessionClosed(session);
	}
	
	@Override
	public void messageReceived(NextFilter nextFilter, IoSession session,Object message) throws Exception {
		if (message instanceof IoBuffer) {
			IoBuffer buffer = (IoBuffer)session.getAttribute(MESSAGE_BUFFER);
				
			buffer.put((IoBuffer) message);			
			int plen = buffer.getInt(0);
			int delta = buffer.position() - (plen+4);
			if(plen > 0)
			{
				if (buffer.position() >= plen+4) {
					byte[] tmp = new byte[plen+4];
					buffer.position(0);
					buffer.get(tmp);
					buffer.compact();
					buffer.position(delta);
					session.setAttribute(MESSAGE_BUFFER,buffer);
					IoBuffer msg = IoBuffer.wrap(tmp);
					nextFilter.messageReceived(session, msg);
				}
			}
			else
			{
				LOGGER.log(Level.INFO,"Packet has invalid length: {0}",plen);
			}
			return;
		}
		else
		{
			LogHelper.info(this, "ERROR: Expected IoBuffer but received {0}.",  message.getClass().getName());
		}
	}
}
