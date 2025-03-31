import com.alibaba.fastjson2.JSONObject;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

public class TestForAvia {

    public static void main(String[] args) {
        GroovyShell groovyShell = new GroovyShell();
        Script script = groovyShell.parse("return data.file.get(0).dataSize>=0;");
        script.setProperty("data",new JSONObject());
        Object o = script.run();
        System.out.printf(o.toString());
    }
}
