package myalgoritm;
import org.tartarus.snowball.SnowballProgram;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by anna on 02.12.16.
 */
public class Snow {
    public static String stemSnowBall(String str) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        Class stemClass = Class.forName("org.tartarus.snowball.ext." +
                "English" + "Stemmer");
        Method stemMethod = stemClass.getMethod("stem", new Class[0]);
        SnowballProgram stemmer = (SnowballProgram) stemClass.newInstance();
        Object [] emptyArgs = new Object[0];
        stemmer.setCurrent(str);
        stemMethod.invoke(stemmer,emptyArgs);
        str=stemmer.getCurrent();
        //System.out.println(str);
        return str;
    }

    public static void main(String[] args) {
        try {
            System.out.println(stemSnowBall("who'll"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}

