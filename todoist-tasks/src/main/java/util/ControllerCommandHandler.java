package util;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import context.MServiceCtx;
import io.github.cdimascio.dotenv.Dotenv;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

public class ControllerCommandHandler extends ChannelInboundHandlerAdapter {
	static private String classesPath = "src/main/java/core/";
	static private String pkgName = "core";
	private CustomClassLoader loader;

	public ControllerCommandHandler() {
		loader = new CustomClassLoader();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ByteBuf msgBuf = (ByteBuf) msg;
		int length = msgBuf.readableBytes();
		String str = msgBuf.toString(Charset.forName("utf-8"));
		if (length > 10 && !str.startsWith("rem")) {
			// java file was sent to add a new command
			putServiceCmd(ctx, msgBuf);
		} else {
			String cmd = msgBuf.toString(Charset.forName("utf-8"));
			String[] tokens = cmd.split("-");
	        try {
				switch (tokens[0]) {
				case "ping":
					pingAppCmd(ctx);
					break;
				case "frez":
					freezAppCmd(ctx);
					break;
				case "cont":
					continueAppCmd(ctx);
					break;
				case "thrdc":
					setAppThreadCountCmd(ctx, Integer.parseInt(tokens[1]));
					break;
				case "rem":
					deleteServiceCmd(ctx, tokens[1]);
					break;
				case "intro":
					introspect(ctx);
					break;
				default:
					ctx.writeAndFlush(Unpooled.copiedBuffer("unknown command: " + cmd, CharsetUtil.UTF_8));
					break;
				}	        	
	        } catch(Exception e) {
	        	e.printStackTrace();
	        	ctx.writeAndFlush(Unpooled.copiedBuffer("err: " + e.getMessage(), CharsetUtil.UTF_8));
	        }
		}

	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}

	private void deleteServiceCmd(ChannelHandlerContext ctx, String serviceName) {
		String className = CommandMapper.getInstance().findClass(serviceName).getName();
		className = className.split("\\.")[1];
		// remove from CommandMapper
		Boolean removed = CommandMapper.getInstance().removeCommand(serviceName);
		
		// remove from src 
		System.out.println(classesPath + className + ".java");
		File file = new File(classesPath + className + ".java");
		removed = removed && file.delete();
		
		String reply = removed? "service removed":"service not found";
		
		ctx.writeAndFlush(Unpooled.copiedBuffer(reply, CharsetUtil.UTF_8));
	}

	private void freezAppCmd(ChannelHandlerContext ctx) throws Exception {
		QueueConsumer.pauseConsumerChannel();
		ThreadPool.stopAllThreads();
		ctx.writeAndFlush(Unpooled.copiedBuffer("freezed app", CharsetUtil.UTF_8));
	}

	private void continueAppCmd(ChannelHandlerContext ctx) throws Exception {
		ThreadPool.startAllThreads();
		QueueConsumer.consumeIncomingMesseges(Dotenv.load(), MServiceCtx.getInstance(), ThreadPool.getThreadPool(), CommandMapper.getInstance());
		ctx.writeAndFlush(Unpooled.copiedBuffer("continued app", CharsetUtil.UTF_8));
	}

	private void setAppThreadCountCmd(ChannelHandlerContext ctx, int count) throws Exception {

		ThreadPool.changeNumOfThreads(count);
		ctx.writeAndFlush(Unpooled.copiedBuffer("changed thread count to " + count, CharsetUtil.UTF_8));
	}

	private void pingAppCmd(ChannelHandlerContext ctx) {
		ctx.writeAndFlush(Unpooled.copiedBuffer("pong", CharsetUtil.UTF_8));
	}
	
	private void putServiceCmd(ChannelHandlerContext ctx, ByteBuf buffer) throws Exception {
		FileOutputStream fos;

		int length = buffer.readableBytes();
		File file = new File(classesPath + "tmp.java");
		fos = new FileOutputStream(file);
		String className = getClassName(buffer);
		String serviceName = getServiceName(className);
		buffer.readBytes(fos, length);
		fos.flush();

		// writing the .java file
		File namedfile = new File(classesPath + className + ".java");
		if (!namedfile.exists()) {
			// Create a new command
			namedfile.createNewFile();
		} else {
			// Update an existing command
			namedfile.delete();
			namedfile.createNewFile();
		}
		file.renameTo(new File(classesPath + className + ".java"));

		// freeing resources
		fos.close();
		fos = null;
		buffer.clear();

		// Compiling the .java file into .class file
		loader.compile(namedfile);

		// Using the new class
		Class<?> c = loader.findClass(pkgName + "." + className, classesPath + className);
		CommandMapper.getInstance().addCommand(serviceName, c);
		
		ctx.writeAndFlush(Unpooled.copiedBuffer(className + " class added", CharsetUtil.UTF_8));
		
	}

	private void introspect(ChannelHandlerContext ctx) throws Exception {
		ctx.writeAndFlush(Unpooled.copiedBuffer(CommandMapper.getInstance().getAllCommands(), CharsetUtil.UTF_8));
	}

	private String getClassName(ByteBuf buf) {

		Pattern p = Pattern.compile("class \\w+");
		Matcher m = p.matcher(buf.toString(Charset.forName("utf-8")));
		String match;
		String[] tokens;

		if (m.find()) {
			match = m.group();
			tokens = match.split(" ");
			if (tokens.length != 2) {
				System.out.println("got wrong match: " + match);
				return "";
			}
			return tokens[1];
		}
		return "";
	}
	
	public static String getServiceName(String string) {
	    String s =  string == null || string.isEmpty() ? "" : Character.toLowerCase(string.charAt(0)) + string.substring(1);
	    s = s.length() > 7 ? s.substring(0, s.length()-7):s;
	    return s;
	}
}
