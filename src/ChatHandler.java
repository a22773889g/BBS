import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Vector;

public class ChatHandler extends Thread {
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	private String user, pwd;
	Account acc;
	boolean flag = false;
	static Vector<Room> roomList = new Vector<Room>(5);
	static Vector online = new Vector(5);
	private Room inviteRoom;
	private String inviter;
	private String help="/list可以來看目前的文章有哪些\r\n/room可以自己建聊天室\r\n/roomlist可以看現在有哪些房間\r\n"
			+ "/chat可以進入聊天室\r\n/article可以發表文章\r\n/look可以觀看文章內容\r\n/update可以更改文章\r\n/delete可以刪出文章\r\n/help可以看有哪些功能\r\n";
	private String roomHelp="您可以使用/invite來邀請其他人進入這個聊天室\r\n可以先使用/user來看看現在有那些人在線上\r\n要離開房間請輸入/exit\r\n/help可以看此房有哪些功能\r\n";
	public ChatHandler(Socket socket) throws IOException {
		this.socket = socket;
		in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "Big5"));
		out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "Big5"));
	}

	public void run() {
		String line;
		synchronized (online) {
			online.add(this);
			this.out.println("歡迎來到lobby,如果第一次用請輸入/regist,如果已有會員請輸入 /login");
			this.out.flush();
		}
		try {
			while (!(line = in.readLine()).equalsIgnoreCase("/quit")) {
				System.out.println(line);
				if (line.equals("/regist")) {
					this.out.println("請輸入帳號:");
					this.out.flush();
					user = this.in.readLine();
					System.out.println(user);
					this.out.println("請入帳密碼:");
					this.out.flush();
					pwd = this.in.readLine();
					System.out.println(pwd);
					if(!(acc=new Account(user, pwd)).checkUser()){
						acc.createAccount();
						this.out.println("註冊成功,請您輸入/login 來登入");
						this.out.flush();
					}
					else{
						this.out.println("此帳戶已有人，請重新輸入/regist來註冊");
						this.out.flush();
					}
				}
				if (line.equals("/login")) {
					this.out.println("請輸入帳號:");
					this.out.flush();
					user = this.in.readLine();
					System.out.println(user);
					this.out.println("請入密碼:");
					this.out.flush();
					pwd = this.in.readLine();
					System.out.println(pwd);
					if ((acc = new Account(user, pwd)).check()) {
						this.out.println("登入成功，您可以使用功能了\r\n"+help);
						this.out.flush();
						while (!(line = in.readLine()).equalsIgnoreCase("/quit")) {
							if (line.equals("/list")) {
								this.out.println(acc.list());
								this.out.flush();
							}
							/*if (line.equals("/article")) {
								String title, content = "";
								this.out.println("請輸入標題:");
								this.out.flush();
								title = this.in.readLine();
								System.out.println(user);
								this.out.println("請入內容,最後以.為結束");
								this.out.flush();
								while (!(line = this.in.readLine()).equalsIgnoreCase(".")) {
									content += line + "\r\n";
								}
								acc.article(title, content);
								this.out.println("發表成功!!\r\n" + acc.list());
								this.out.flush();
							}
							if (line.equals("/look")) {
								String id;
								this.out.println("請問您要看第幾篇文章:");
								this.out.flush();
								id = this.in.readLine();
								this.out.println(acc.look(id));
								this.out.flush();
							}
							if (line.equals("/update")) {
								String id, content = "";
								this.out.println("請問您要修改第幾篇文章:");
								this.out.flush();
								id = this.in.readLine();
								this.out.println("請輸入更改後的內容:");
								this.out.flush();
								while (!(line = this.in.readLine()).equalsIgnoreCase(".")) {
									content += line + "\r\n";
								}
								acc.update(id, content);
								this.out.println("修改成功!!\r\n" + acc.list());
								this.out.flush();
							}
							if (line.equals("/delete")) {
								String id;
								this.out.println("請問您要刪除第幾篇文章:");
								this.out.flush();
								id = this.in.readLine();
								if (acc.authenticate(id)) {
									acc.delete(id);
									this.out.println("刪除成功!!\r\n" + acc.list());
									this.out.flush();
								} else {
									this.out.println("您不是作者,所以不能刪除此文章");
									this.out.flush();
								}
							}*/
							if (line.equals("/room")) {
								String message;
								this.out.println("請輸入房間名稱:");
								this.out.flush();
								Room room = new Room(this.in.readLine());
								synchronized (room.room) {
									room.room.addElement(this);
									roomList.addElement(room);
								}
								this.out.println("您已進入" + room.name + "房\r\n");
								this.out.flush();
								this.out.println(roomHelp);
								this.out.flush();
								while (!(message = this.in.readLine()).equalsIgnoreCase("/exit")) {
									if(message.equals("/help")){
										this.out.print(roomHelp);
										this.out.flush();
									}
									if (message.equals("/user")) {
										String onlineList = "";
										int count = 0;
										for (int i = 0; i < online.size(); i++) {
											synchronized (online) {
												ChatHandler handler = (ChatHandler) online.elementAt(i);
												if (handler != this) {
													count++;
													onlineList += count + ". " + handler.acc.user + "\r\n";
												}
											}
										}
										this.out.println(onlineList);
										this.out.flush();
									} /*else if (message.equals("/invite")) {
										this.out.println("請輸入要邀請的人的名稱:");
										this.out.flush();
										String user = this.in.readLine();
										boolean check=false;
										for (int i = 0; i < online.size(); i++) {
											ChatHandler handler = (ChatHandler) online.elementAt(i);
											if (handler.acc.user.equals(user)) {
												check=true;
												handler.flag = true;
												handler.inviteRoom = room;
												handler.inviter=this.acc.user;
												handler.out.println(this.acc.user + "邀請您進他的聊天室，進入請輸入y，不進入請輸入n");
												handler.out.flush();
												break;
											}
										}
										if(check==false){
											this.out.println("目前線上沒有這個人，請重新使用/invite並輸入正確名稱\r\n");
											this.out.flush();
										}
									}*/ else {
										for (int i = 0; i < room.room.size(); i++) {
											synchronized (room.room) {
												ChatHandler handler = (ChatHandler) room.room.elementAt(i);
												if (handler != this) {
													handler.out.println(acc.user + ": " + message + "\r");
													handler.out.flush();
												}
											}
										}
									}
								}
								synchronized (room.room) {
									room.room.removeElement(this);
									if (room.room.size() == 0) {
										roomList.removeElement(room);
									}
									this.out.print("您已離開房間\r\n");
									this.out.flush();
								}
							}
							if (line.equals("/roomlist")) {
								if (roomList.size() > 0) {
									for (int i = 0; i < roomList.size(); i++) {
										this.out.print((i + 1) + ". " + roomList.get(i).name + "\r\n");
										this.out.flush();
									}
								} else {
									this.out.print("目前沒有房間，請輸入/room來建立\r\n");
									this.out.flush();
								}
							}
							if (line.equals("y") && flag == true) {
								String message;
								this.flag=false;
								synchronized (this.inviteRoom.room) {
									this.inviteRoom.room.addElement(this);
									this.out.println("您已進入" + inviteRoom.name + "房\r\n");
									this.out.print(roomHelp);
									this.out.flush();
								}
								while (!(message = this.in.readLine()).equalsIgnoreCase("/exit")) {
									if(message.equals("/help")){
										this.out.print(roomHelp);
										this.out.flush();
									}
									if(message.equals("/help")){
										this.out.print(roomHelp);
										this.out.flush();
									}
									if (message.equals("/user")) {
										String onlineList = "";
										int count = 0;
										for (int i = 0; i < online.size(); i++) {
											synchronized (online) {
												ChatHandler handler = (ChatHandler) online.elementAt(i);
												if (handler != this) {
													count++;
													onlineList += count + ". " + handler.acc.user + "\r\n";
												}
											}
										}
										this.out.println(onlineList);
										this.out.flush();
									} else if (message.equals("/invite")) {
										this.out.println("請輸入要邀請的人的名稱:");
										this.out.flush();
										String user = this.in.readLine();
										boolean check=false;
										for (int i = 0; i < online.size(); i++) {
											ChatHandler handler = (ChatHandler) online.elementAt(i);
											if (handler.acc.user.equals(user)) {
												check=true;
												handler.flag = true;
												handler.inviteRoom = this.inviteRoom;
												handler.inviter=this.acc.user;
												handler.out.println(this.acc.user + "邀請您進他的聊天室，進入請輸入y，不進入請輸入n");
												handler.out.flush();
											}
										}
										if(check==false){
											this.out.println("目前線上沒有這個人，請重新使用/invite並輸入正確名稱\r\n");
											this.out.flush();
										}
									} else {
										for (int i = 0; i < this.inviteRoom.room.size(); i++) {
											synchronized (this.inviteRoom.room) {
												ChatHandler handler = (ChatHandler) this.inviteRoom.room.elementAt(i);
												if (handler != this) {
													handler.out.println(acc.user + ": " + message + "\r");
													handler.out.flush();
												}
											}
										}
										System.out.println(this.inviteRoom.room.size());
									}
								}
								synchronized (this.inviteRoom.room) {
									this.inviteRoom.room.removeElement(this);
									if (this.inviteRoom.room.size() == 0) {
										roomList.removeElement(this.inviteRoom.room);
									}
									this.out.print("您已離開房間\r\n");
									this.out.flush();
								}
							}
							if(line.equals("n") && flag==true){
								this.flag=false;
								for(int i =0;i<online.size();i++){
									synchronized (online) {
										ChatHandler handler = (ChatHandler) online.elementAt(i);
										if (this.inviter.equals(handler.acc.user)) {
											handler.out.println(this.user+"拒絕您的邀請");
											handler.out.flush();
										}
									}
								}
							}
							if(line.equals("/help")){
								this.out.print(help);
								this.out.flush();
							}
							if (line.equals("/chat")) {
								String message;
								int id;
								this.out.print("請輸入您要進入第幾間房間:\r\n");
								this.out.flush();
								id = Integer.parseInt(this.in.readLine()) - 1;
								this.out.print("\r\n已進入" + roomList.get(id).name + "房\r\n");
								this.out.flush();
								synchronized (roomList.get(id).room) {
									roomList.get(id).room.addElement(this);
								}
								this.out.println(roomHelp);
								this.out.flush();
								while (!(message = this.in.readLine()).equalsIgnoreCase("/exit")) {
									if(message.equals("/help")){
										this.out.print("\r\n已進入" + roomList.get(id).name + "房\r\n");
										this.out.flush();
									}
									if (message.equals("/user")) {
										String onlineList = "";
										int count = 0;
										for (int i = 0; i < online.size(); i++) {
											synchronized (online) {
												ChatHandler handler = (ChatHandler) online.elementAt(i);
												if (handler != this) {
													count++;
													onlineList += count + ". " + handler.acc.user + "\r\n";
												}
											}
										}
										this.out.println(onlineList);
										this.out.flush();
									} else if (message.equals("/invite")) {
										this.out.println("請輸入要邀請的人的名稱:");
										this.out.flush();
										String user = this.in.readLine();
										boolean check=false;
										for (int i = 0; i < online.size(); i++) {
											ChatHandler handler = (ChatHandler) online.elementAt(i);
											if (handler.acc.user.equals(user)) {
												check=true;
												handler.flag = true;
												handler.inviteRoom = roomList.get(id);
												handler.inviter=this.acc.user;
												handler.out.println(this.acc.user + "邀請您進他的聊天室，進入請輸入y，不進入請輸入n");
												handler.out.flush();
											}
										}
										if(check==false){
											this.out.println("目前線上沒有這個人，請重新使用/invite並輸入正確名稱\r\n");
											this.out.flush();
										}
									} else {
										for (int i = 0; i < roomList.get(id).room.size(); i++) {
											synchronized (roomList.get(id).room) {
												ChatHandler handler = (ChatHandler) roomList.get(id).room.elementAt(i);
												if (handler != this) {
													handler.out.println(acc.user + ": " + message + "\r");
													handler.out.flush();
												}
											}
										}
										System.out.println(roomList.get(id).room.size());
									}
								}
								synchronized (roomList.get(id).room) {
									roomList.get(id).room.removeElement(this);
									if (roomList.get(id).room.size() == 0) {
										roomList.removeElement(roomList.get(id).room);
									}
									this.out.print("您已離開房間\r\n");
									this.out.flush();
								}
							}
						}

					} else {
						this.out.println("登入失敗,您輸入的帳號或密碼錯誤");
						this.out.flush();
					}
				}
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				online.removeElement(this);
				in.close();
				out.close();
				socket.close();
			} catch (IOException ioe) {
			}
		}
	}
}