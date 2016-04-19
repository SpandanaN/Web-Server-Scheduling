
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CalWtsTest {

    public static void main(String[] args) {
        ArrayList<String> queids = new ArrayList();
        queids.add("abc");
        queids.add("def");
        queids.add("ghi");
        CalculateWeights c = new CalculateWeights(queids);
        try {
            
            int i = 0;
            while(i < 10)
            {
                ArrayList<Float> weights = c.getWeights();
                System.out.println("Weights after "+ i +"  secs : ");
                for(float f : weights){
                    System.out.println(f);
                }
                i++;
                Thread.sleep(1000);
            }
            
        } catch (InterruptedException ex) {
            Logger.getLogger(CalWtsTest.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
