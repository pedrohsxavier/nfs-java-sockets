package br.edu.ifpb.pedro.so.sockets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Servidor {
	
	private static String HOME = "E:\\teste\\";

    public static void main(String[] args) throws IOException {
        System.out.println("== Servidor ==");
        
        Servidor server = new Servidor();

        // Configurando o socket
        ServerSocket serverSocket = new ServerSocket(7001);
        Socket socket = serverSocket.accept();

        // pegando uma refer√™ncia do canal de sa√≠da do socket. Ao escrever nesse canal, est√° se enviando dados para o
        // servidor
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        // pegando uma refer√™ncia do canal de entrada do socket. Ao ler deste canal, est√° se recebendo os dados
        // enviados pelo servidor
        DataInputStream dis = new DataInputStream(socket.getInputStream());

        // la√ßo infinito do servidor
        while (true) {
            System.out.println("Cliente: " + socket.getInetAddress());

            String mensagem = dis.readUTF();
            System.out.println(mensagem);
            
            String[] arrayMessage = mensagem.split(" "); 

            //dos.writeUTF("Li sua mensagem: " + mensagem);
            
            switch(arrayMessage[0]) {
            
            	case "readdir":
            		String msg = server.readdir(arrayMessage[1]);
            		if (msg != null) {
            			dos.writeUTF(msg);
            		} else {
            			dos.writeUTF("DiretÛrio ou arquivo n„o existe");
            		}
            		
            		break;
            		
            		
            	case "rename":
            		if (server.rename(arrayMessage[1], arrayMessage[2])) {
            			dos.writeUTF("Arquivo renomeado com sucesso!");
            		} else {
            			dos.writeUTF("DiretÛrio ou arquivo n„o existe");
            		}
            		
            		break;
            		
            		
            	case "create":
            		if (server.create(arrayMessage[1])) {
            			dos.writeUTF("Arquivo criado com sucesso!");
            		} else {
            			dos.writeUTF("Arquivo j· existe");
            		}
            		
            		break;
            		
            		
            	case "remove":
            		if (server.remove(arrayMessage[1])) {
            			dos.writeUTF("Arquivo removido com sucesso!");
            		} else {
            			dos.writeUTF("Arquivo n„o existe");
            		}
            		
            		break;
            
            		
            	default:
            		dos.writeUTF(arrayMessage[0] + " comando n„o reconhecido.");
            		break;
            }
        }
        /*
         * Observe o while acima. Perceba que primeiro se l√™ a mensagem vinda do cliente (linha 29, depois se escreve
         * (linha 32) no canal de sa√≠da do socket. Isso ocorre da forma inversa do que ocorre no while do Cliente2,
         * pois, de outra forma, daria deadlock (se ambos quiserem ler da entrada ao mesmo tempo, por exemplo,
         * ningu√©m evoluiria, j√° que todos estariam aguardando.
         */
    }
    
    public String readdir (String diretorio) throws IOException {
    	Path p = Paths.get(HOME+diretorio);
    		if(Files.exists(p)) {
    			Stream <Path> list = Files.list(p);
    			return list.map(Path::getFileName)
    					   .map(Object::toString)
    					   .collect(Collectors.joining(", "));
    		} else {
    			return null;
    		}
    }
    
    public boolean rename (String arquivo, String nomeNovo) throws IOException {
    	Path p1 = Paths.get(HOME+arquivo);
    	Path p2 = Paths.get(HOME+nomeNovo);
    		if (Files.exists(p1)) {
    			Files.move(p1, p2);
    			return true;
    		} else {
    			return false;
    		}
    }
    
    public boolean create (String arquivoNome) throws IOException {
    	Path p = Paths.get(HOME + "\\" + arquivoNome);
    	if (!Files.exists(p)) {
    		return true;
    	} else {
    		return false;
    	}
    }
    
    public boolean remove (String arquivoNome) throws IOException {
    	Path p = Paths.get(HOME + arquivoNome);
    	if (Files.exists(p)) {
    		Files.delete(p);
    		return true;
    	} else {
    		return false;
    	}
    }
}
