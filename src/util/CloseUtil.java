package util;

import java.net.Socket;

public class CloseUtil {
  public static void close(Socket socket) {
      try {
          socket.close();
      }catch (Exception e){
          e.printStackTrace();
      }
  }
}
