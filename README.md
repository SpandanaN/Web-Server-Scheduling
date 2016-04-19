# Multi-core Web-Server Scheduling
<p>Any website is expected to deal with a large number of requests simultaneously in a reasonable mean response time. In order to improve performance of handling user requests, most of web servers adopt multi­core processors. Although FCFS is a reasonable and fair strategy for requests handling, it doesn’t exploit the characteristic of multi­core CPUs into account. In the multicore servers, multiple threads are created to run on each core. There will be situations where two threads running on different cores have access to same­shared data, which can cause cache incoherence in the web server. This is called ping­pong effect, which is solved by the SWRS model using affinity to cores. The SWRS model causes load imbalance over a long period. To solve the problem of ping­pong effect and load imbalance, we proposed a modified algorithm, called DWRS based on dynamic weights of request queues. Our experiment results show that the DWRS algorithm could solve the load imbalance between cores in long period and avoid the problem of ping­pong effect in multi­core systems.</p>

# Algorithm Design
<h5>SWRS</h5>
<ul>
<li>Calculate the weights from the above two calculations</li>
<li>Calculate the number of threads to be assigned to each core</li>
<li>Assign threads to each core</li>
<li>Start the server</li>
</ul>
<h5>DWRS</h5>
<ul>
<li>Start the server with initial weights and threads assigned to each core</li>
<li>Get the mean service time of each request queue from the logger class</li>
<li>Calculate predicted frequency for each request queue</li>
<li>Calculate the weights from frequency and mean service time</li>
<li>Update the weights after every N requests (in our case 50) and calculate the number of
threads to be assigned to each core</li>
<li>Re-assign threads to each core</li>
</ul>

#Implementation
<h5>Implementation Details</h5>
<p>Simulated a server with 3 request queues, 4 cores and 100 thread in the server threadpool. This simulated server can handle three types of requests: Google requests, Facebook requests and Amazon requests. The processing of each of these requests involves connecting to the respective websites and retrieving their webpage html code. Implemented FCFS, SWRS and DWRS scheduling strategies on the simulated webserver and compared their performances.</p>
<h5>Language Used</h5>
<p>Implemented all code in JAVA. For implementing hard-affinity in SWRS and DWRS , used the OpenHFT Java thread affinity Library.</p>
<h5>Testing</h5>
<p>Used Apache JMeter to test the performance of all implemented scheduling strategies and compare their results under different workloads and represent the results in the form of graphs.</p>

#Results
<h5>FCFS vs DWRS</h5>
<p>Tested the performance of FCFS and DWRS by sending a total of 600 requests to the server. It included 100 Google Requests, 200 Facebook Requests and 300 Amazon Requests. Observed that the mean response time for FCFS implementation is 372 ms while it is only 227 ms for DWRS implementation.</p>
<h5>SWRS vs DWRS</h5>
<p>Compared the performance of SWRS and DWRS by running both the implementations under different workloads. Observed that the for smaller workload, the response time of SWRS was better than DWRS because of the weight calculation overhead for the DWRS. As, the workload increased, DRWS performance increased as the overhead effect reduced. And for long time period, with more workload DWRS showed slightly better performance than SWRS. This shows the load balancing results of DWRS over long-time periods.</p>

#Future Scope
<p>Tried to solve the load imbalance problem by dynamically calculating weights and re-allocating threads. Though the DWRS showed slightly better performance than SWRS for long-time periods, the performance of DWRS can be further improved . There is scope for more research to improve the efficiency of the thread allocation technique. Load balancing can be improved with better thread allocation techniques and evenly distributing the threads over the cores.</p>


