import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import static java.lang.Math.abs;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is going to calculate the weights
 **/
public class CalculateWeights implements Runnable {
    /**
     * Parameters of the class
     */
    ArrayList<RequestDetails> reqQues;
    int timeinterval = 1000;
    int curtime;
    Thread th;
    HashMap<RequestDetails, Integer> curNoOfReq;
    /**
     * Constructor for the class
     */
    public CalculateWeights(ArrayList<String> reqQueids){
    	th = new Thread(this, "WeightCal");
        curNoOfReq = new HashMap();
        reqQues = new ArrayList();
        for (String reqQueid : reqQueids) {
            RequestDetails r = new RequestDetails(reqQueid);
            reqQues.add(r);
        }
        for(int i = 0; i< reqQues.size(); i++){
            curNoOfReq.put(reqQues.get(i),0);
        }
        curtime = 1;
        th.start();
    }
    public ArrayList<Float> getWeights(){
        ArrayList<Float> weights = new ArrayList();
        for(RequestDetails r:reqQues){
            float w = r.getWeights();
            weights.add(w);
        }
        return weights;
    }
    /**
     * This function will create a RequestDetails Object
     * if it has to be added dynamically after creating this class
     * Input arguments : String (Request Queue ID)
     * Return type void
     */
    public void AddReqQue(String reqQueName){
        reqQues.add(new RequestDetails(reqQueName));
    }
    
    /**
     * This function will run by thread.start()
     * This function will calculate weights of request queues
     * dynamically.
     * Input arguments : void
     * Return type     : void
     */
    @Override
    public void run() {
        
    	 while(true)
    	 {
             curtime+= timeinterval;
            for(RequestDetails r:reqQues){
                PopReqDetails();
                r.calWeights();
            }
            try {
                Thread.sleep(timeinterval);
            } catch (InterruptedException ex) {
                ex.printStackTrace(System.out);
            }
    	 }
    }
    
    /**
     * This function will read the log file and run the code
     * 
     */
    void PopReqDetails(){
        int timePeriod = 1;
        
        
            ArrayList<Integer> val=new ArrayList<Integer>();
            val.add(Server.googleQueueCounter-curNoOfReq.get(reqQues.get(0)));
            val.add(Server.facebookQueueCounter-curNoOfReq.get(reqQues.get(1)));
            val.add(Server.amazonQueueCounter-curNoOfReq.get(reqQues.get(2)));
            
            
            for(int i=0;i<reqQues.size();i++)
            {
            curNoOfReq.put(reqQues.get(i),val.get(i));
            }
           
        
        
        Iterator iter = curNoOfReq.entrySet().iterator();
        while (iter.hasNext()){
            Map.Entry entry = (Map.Entry)iter.next();
            int value = (Integer)entry.getValue();
            float freq = (float) value/timeinterval;
           
            ((RequestDetails)entry.getKey()).setCurFrequency(freq);
        }
    }
    
    /**
     * this class contains the details for each request queue
     */
    public class RequestDetails{
        float PredFreqFut;
        float ActFreqCur;
        float ActFreqPast;
        String ReqQueID;
        float SmoothedAbsErrorCur;
        float SmoothedErrorCur;
        float meanServTime;
        float weight;
        int Totalrunningtime;
        int noOfRequestperqueue;
        
        public RequestDetails(String reqQueID){
            ReqQueID = reqQueID;
            noOfRequestperqueue = 0;
            SmoothedAbsErrorCur = 0;
            SmoothedErrorCur = 0;
            Totalrunningtime = 0;
            PredFreqFut = 0;
            meanServTime = 235;
        }
        
        public void calWeights(){
            calPredFreqFut();
            weight = meanServTime * PredFreqFut;
        }
        
        public float getWeights(){
            return weight;
        }
        
        public void setCurFrequency(float freq){
            ActFreqPast = ActFreqCur;
            ActFreqCur = freq;
        }
        public void addTotalRunningTime(int millis){
            Totalrunningtime += millis;
            noOfRequestperqueue++;
        }
        
        private void calPredFreqFut(){
            float PredFreqCur = PredFreqFut;
            float SmoothedAbsErrorPast = SmoothedAbsErrorCur; //M(t-2) = M(t-2)
            float SmoothedErrorPast = SmoothedErrorCur;       //E(t-2) = E(t-1)
            float alpha;                                      //Smoothing constant
            float errCurr = PredFreqCur - ActFreqCur;         //e(t-1)
            float beta = 0.1F;
            SmoothedErrorCur = beta*errCurr + (1-beta)*SmoothedErrorPast;
            SmoothedAbsErrorCur = beta*(abs(errCurr)) + (1-beta)*SmoothedAbsErrorPast;
            if(SmoothedAbsErrorCur!=0)
                alpha = abs(SmoothedErrorCur/SmoothedAbsErrorCur);
            else
                alpha = 1;
            PredFreqFut = PredFreqCur - alpha * errCurr;
            
        }
    }
    
}
