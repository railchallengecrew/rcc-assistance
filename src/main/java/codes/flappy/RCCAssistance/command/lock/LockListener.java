package codes.flappy.RCCAssistance.command.lock;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Channel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.management.openmbean.KeyAlreadyExistsException;
import java.io.*;
import java.util.ArrayList;
import java.util.logging.Logger;

public class LockListener extends ListenerAdapter {
    private static ArrayList<Long> lockedChannels;
    
    public LockListener() {
        lockedChannels = new ArrayList<>();

        try(ObjectInputStream in = new ObjectInputStream(new FileInputStream("locks.dat"))) {
            if (in.readObject() instanceof ArrayList) {
                lockedChannels = (ArrayList<Long>) in.readObject();
            }
        } catch(FileNotFoundException ignored) {}
        catch(IOException ex) {
            Logger.getLogger(LockListener.class.getName()).warning("Could not open locks.dat (IOException): "+ex.getMessage());
        } catch(ClassNotFoundException ex) {
            Logger.getLogger(LockListener.class.getName()).warning("Could not readObject locks.dat (ClassNotFoundException): "+ex.getMessage());
        }
    }
    
    public static void addLockedChannel(Channel channel) throws KeyAlreadyExistsException {
        if (lockedChannels.contains(channel.getIdLong())) throw new KeyAlreadyExistsException("Channel is already locked");
        Logger.getLogger(LockListener.class.getName()).info("Locking channel: "+channel.getName());
        lockedChannels.add(channel.getIdLong());
        saveLockedChannels();
    }
    
    public static void delLockedChannel(Channel channel) {
        Logger.getLogger(LockListener.class.getName()).info("Unlocking channel: "+channel.getName());
        lockedChannels.remove(channel.getIdLong());
        saveLockedChannels();
    }

    private static void saveLockedChannels() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("locks.dat"))) {
            out.writeObject(lockedChannels);
        } catch(FileNotFoundException ex) {
            Logger.getLogger(LockListener.class.getName()).warning("Could not write to locks.dat (FileNotFoundException): "+ex.getMessage());
        } catch(IOException ex) {
            Logger.getLogger(LockListener.class.getName()).warning("Could not write to locks.dat (IOException): "+ex.getMessage());
        }
    }
    
    public void onMessageReceived(MessageReceivedEvent e) {
        if (lockedChannels.contains(e.getChannel().getIdLong())) {
            if (!e.getMember().hasPermission(Permission.MANAGE_CHANNEL)) {
                e.getMessage().delete().queue();
            }
        }
    }
}
