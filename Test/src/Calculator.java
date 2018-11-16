import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
public class Calculator {
	private String inputFile;
	  private String outputFile;
	  private Queue<String> queue;
	  private boolean finish = false;
	  
	  public Calculator(String inputFile, String outputFile) {
	    this.inputFile = inputFile;
	    this.outputFile = outputFile;
	    this.queue = new LinkedList<>();
	  }

	  private String calculate(String str) {
	      int sum = 0;
	      int num = 0;
	      int pre = 1;
	      for(int i = 0; i < str.length(); i++) {
	        char c = str.charAt(i);
	        if(c == ' ') continue;
	        if(Character.isDigit(c)) {
	          num = num * 10 + (int)(c - '0');
	        }else {
	          sum += pre * num;
	          num = 0;
	          pre = (c == '+')?1:-1;
	        }
	      }
	      sum += (pre * num);
	      return str + " = "+sum;
	  }

	  public void start() {
	    
		  Thread writer = new Thread(new Runnable(){
		      @Override
		      public void run() {
		          File file = new File("input.txt");
		          BufferedReader rd = null;

		          try {
		              rd = new BufferedReader(new FileReader(file));
		              String text = null;

		            while((text = rd.readLine()) != null) {
		            	if(text.isEmpty()) continue;
		            	System.out.println("write: "+text);
		            	queue.offer(calculate(text));
		            	synchronized (queue) {
			              queue.notify();
		            	}
		            }
		            finish = true;
		          } catch (FileNotFoundException e) {
		            e.printStackTrace();
		          } catch (IOException e) {
		            e.printStackTrace();
		          } finally {
		            try {
		              if(rd != null) {
		                rd.close();
		              }
		            } catch (IOException e) {

		            }
		          }
		      }
		    });
		  writer.start();

		    Thread reader = new Thread(new Runnable(){
		      @Override
		      public void run() {
		    	  BufferedWriter writer = null;
				try {
					writer = new BufferedWriter(new FileWriter(outputFile));
					while(!finish) {
						if(queue.isEmpty()) {
							synchronized (queue) {
								queue.wait();
							}
						}
						String str = queue.poll();
						System.out.println("read: "+str);
			    		writer.write(str+"\n");
			    	  }
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					
						try {
							if( writer != null) {
							writer.close();
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
		    	 
		    	
		    });
		  reader.start();
		  
	  }
	  
	  public static void main(String[] args) {
		   Calculator cal = new Calculator("input.txt","output.txt");
		   cal.start();
	}
}
