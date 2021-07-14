package util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

public class CustomClassLoader extends ClassLoader 
{
 
    public static final String EXT = ".class";
    JavaCompiler compiler;
 
	public CustomClassLoader()
    {

    	compiler = ToolProvider.getSystemJavaCompiler();
    }
    
    @Override
    public Class findClass(String name,String path) {
        byte[] b = new byte[0];
        try {
            b = loadClassFromFile(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return defineClass(name, b, 0, b.length);
    }

    private byte[] loadClassFromFile(String path) throws FileNotFoundException {
        File file = new File(path + EXT);
        InputStream inputStream = new FileInputStream(file);
        byte[] buffer;
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        int nextValue = 0;
        try {
            while ( (nextValue = inputStream.read()) != -1 ) {
                byteStream.write(nextValue);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        buffer = byteStream.toByteArray();

        return buffer;
    }
    
    public void compile(File srcFile) {
        try {
            compiler.run(null, null, null, srcFile.getPath());
            System.out.println("compiled...");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
