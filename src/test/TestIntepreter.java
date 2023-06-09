import org.junit.Test;
import util.Interpreter;

import static org.junit.Assert.assertEquals;


public class TestIntepreter {

    @Test
    public void testInterString() {
        String testForString = "tjy\"test1\" tyytky tkk";
        String res = Interpreter.string(testForString.toCharArray(), 4);
        assertEquals("test1", res);
    }

    @Test
    public void testInterPositive(){
        String testForPositiveNum = "wqeq32w1234";
        Number res1 = Interpreter.number(testForPositiveNum.toCharArray(), 4);
        assertEquals(32.0, res1);
        Number res2 = Interpreter.number(testForPositiveNum.toCharArray(), 7);
        assertEquals(1234.0, res2);
    }

    @Test
    public void testInterNegative(){
        String testForNegativeNum = "--sad-dsad-3-24sd23-s-321";
        Number tes1 =  Interpreter.number(testForNegativeNum.toCharArray(), 10);
        assertEquals(-3.0, tes1);
        Number tes2 = Interpreter.number(testForNegativeNum.toCharArray(), 21);
        assertEquals(-321.0, tes2);
    }


}