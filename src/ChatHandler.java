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
	private String help="/list�i�H�Ӭݥثe���峹������\r\n/room�i�H�ۤv�ز�ѫ�\r\n/roomlist�i�H�ݲ{�b�����ǩж�\r\n"
			+ "/chat�i�H�i�J��ѫ�\r\n/article�i�H�o��峹\r\n/look�i�H�[�ݤ峹���e\r\n/update�i�H���峹\r\n/delete�i�H�R�X�峹\r\n/help�i�H�ݦ����ǥ\��\r\n";
	private String roomHelp="�z�i�H�ϥ�/invite���ܽШ�L�H�i�J�o�Ӳ�ѫ�\r\n�i�H���ϥ�/user�Ӭݬݲ{�b�����ǤH�b�u�W\r\n�n���}�ж��п�J/exit\r\n/help�i�H�ݦ��Ц����ǥ\��\r\n";
	public ChatHandler(Socket socket) throws IOException {
		this.socket = socket;
		in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "Big5"));
		out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "Big5"));
	}

	public void run() {
		String line;
		synchronized (online) {
			online.add(this);
			this.out.println("�w��Ө�lobby,�p�G�Ĥ@���νп�J/regist,�p�G�w���|���п�J /login");
			this.out.flush();
		}
		try {
			while (!(line = in.readLine()).equalsIgnoreCase("/quit")) {
				System.out.println(line);
				if (line.equals("/regist")) {
					this.out.println("�п�J�b��:");
					this.out.flush();
					user = this.in.readLine();
					System.out.println(user);
					this.out.println("�ФJ�b�K�X:");
					this.out.flush();
					pwd = this.in.readLine();
					System.out.println(pwd);
					if(!(acc=new Account(user, pwd)).checkUser()){
						acc.createAccount();
						this.out.println("���U���\,�бz��J/login �ӵn�J");
						this.out.flush();
					}
					else{
						this.out.println("���b��w���H�A�Э��s��J/regist�ӵ��U");
						this.out.flush();
					}
				}
				if (line.equals("/login")) {
					this.out.println("�п�J�b��:");
					this.out.flush();
					user = this.in.readLine();
					System.out.println(user);
					this.out.println("�ФJ�K�X:");
					this.out.flush();
					pwd = this.in.readLine();
					System.out.println(pwd);
					if ((acc = new Account(user, pwd)).check()) {
						this.out.println("�n�J���\�A�z�i�H�ϥΥ\��F\r\n"+help);
						this.out.flush();
						while (!(line = in.readLine()).equalsIgnoreCase("/quit")) {
							if (line.equals("/list")) {
								this.out.println(acc.list());
								this.out.flush();
							}
							/*if (line.equals("/article")) {
								String title, content = "";
								this.out.println("�п�J���D:");
								this.out.flush();
								title = this.in.readLine();
								System.out.println(user);
								this.out.println("�ФJ���e,�̫�H.������");
								this.out.flush();
								while (!(line = this.in.readLine()).equalsIgnoreCase(".")) {
									content += line + "\r\n";
								}
								acc.article(title, content);
								this.out.println("�o���\!!\r\n" + acc.list());
								this.out.flush();
							}
							if (line.equals("/look")) {
								String id;
								this.out.println("�аݱz�n�ݲĴX�g�峹:");
								this.out.flush();
								id = this.in.readLine();
								this.out.println(acc.look(id));
								this.out.flush();
							}
							if (line.equals("/update")) {
								String id, content = "";
								this.out.println("�аݱz�n�ק�ĴX�g�峹:");
								this.out.flush();
								id = this.in.readLine();
								this.out.println("�п�J���᪺���e:");
								this.out.flush();
								while (!(line = this.in.readLine()).equalsIgnoreCase(".")) {
									content += line + "\r\n";
								}
								acc.update(id, content);
								this.out.println("�ק令�\!!\r\n" + acc.list());
								this.out.flush();
							}
							if (line.equals("/delete")) {
								String id;
								this.out.println("�аݱz�n�R���ĴX�g�峹:");
								this.out.flush();
								id = this.in.readLine();
								if (acc.authenticate(id)) {
									acc.delete(id);
									this.out.println("�R�����\!!\r\n" + acc.list());
									this.out.flush();
								} else {
									this.out.println("�z���O�@��,�ҥH����R�����峹");
									this.out.flush();
								}
							}*/
							if (line.equals("/room")) {
								String message;
								this.out.println("�п�J�ж��W��:");
								this.out.flush();
								Room room = new Room(this.in.readLine());
								synchronized (room.room) {
									room.room.addElement(this);
									roomList.addElement(room);
								}
								this.out.println("�z�w�i�J" + room.name + "��\r\n");
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
										this.out.println("�п�J�n�ܽЪ��H���W��:");
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
												handler.out.println(this.acc.user + "�ܽбz�i�L����ѫǡA�i�J�п�Jy�A���i�J�п�Jn");
												handler.out.flush();
												break;
											}
										}
										if(check==false){
											this.out.println("�ثe�u�W�S���o�ӤH�A�Э��s�ϥ�/invite�ÿ�J���T�W��\r\n");
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
									this.out.print("�z�w���}�ж�\r\n");
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
									this.out.print("�ثe�S���ж��A�п�J/room�ӫإ�\r\n");
									this.out.flush();
								}
							}
							if (line.equals("y") && flag == true) {
								String message;
								this.flag=false;
								synchronized (this.inviteRoom.room) {
									this.inviteRoom.room.addElement(this);
									this.out.println("�z�w�i�J" + inviteRoom.name + "��\r\n");
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
										this.out.println("�п�J�n�ܽЪ��H���W��:");
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
												handler.out.println(this.acc.user + "�ܽбz�i�L����ѫǡA�i�J�п�Jy�A���i�J�п�Jn");
												handler.out.flush();
											}
										}
										if(check==false){
											this.out.println("�ثe�u�W�S���o�ӤH�A�Э��s�ϥ�/invite�ÿ�J���T�W��\r\n");
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
									this.out.print("�z�w���}�ж�\r\n");
									this.out.flush();
								}
							}
							if(line.equals("n") && flag==true){
								this.flag=false;
								for(int i =0;i<online.size();i++){
									synchronized (online) {
										ChatHandler handler = (ChatHandler) online.elementAt(i);
										if (this.inviter.equals(handler.acc.user)) {
											handler.out.println(this.user+"�ڵ��z���ܽ�");
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
								this.out.print("�п�J�z�n�i�J�ĴX���ж�:\r\n");
								this.out.flush();
								id = Integer.parseInt(this.in.readLine()) - 1;
								this.out.print("\r\n�w�i�J" + roomList.get(id).name + "��\r\n");
								this.out.flush();
								synchronized (roomList.get(id).room) {
									roomList.get(id).room.addElement(this);
								}
								this.out.println(roomHelp);
								this.out.flush();
								while (!(message = this.in.readLine()).equalsIgnoreCase("/exit")) {
									if(message.equals("/help")){
										this.out.print("\r\n�w�i�J" + roomList.get(id).name + "��\r\n");
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
										this.out.println("�п�J�n�ܽЪ��H���W��:");
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
												handler.out.println(this.acc.user + "�ܽбz�i�L����ѫǡA�i�J�п�Jy�A���i�J�п�Jn");
												handler.out.flush();
											}
										}
										if(check==false){
											this.out.println("�ثe�u�W�S���o�ӤH�A�Э��s�ϥ�/invite�ÿ�J���T�W��\r\n");
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
									this.out.print("�z�w���}�ж�\r\n");
									this.out.flush();
								}
							}
						}

					} else {
						this.out.println("�n�J����,�z��J���b���αK�X���~");
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