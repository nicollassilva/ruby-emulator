package com.cometproject.server.game.commands.user;

import com.cometproject.server.config.Locale;
import com.cometproject.server.game.commands.ChatCommand;
import com.cometproject.server.network.sessions.Session;
import org.apache.tomcat.jni.Local;

public class NitroCommand extends ChatCommand {
    public final static String NITRO="command_nitro";
    @Override
    public void execute(Session client, String[] params) {
        final boolean flag = !client.getPlayer().getEntity().hasAttribute(NITRO);
        if(flag){
            sendWhisper(Locale.getOrDefault("command.nitro.on","Comando nitro ativo."),client);
            client.getPlayer().getEntity().setAttribute(NITRO, "1");
        }
        else{
            sendWhisper(Locale.getOrDefault("command.nitro.off","Comando nitro desativado."), client);
            client.getPlayer().getEntity().removeAttribute(NITRO);
        }
    }

    @Override
    public String getPermission() {
        return "nitro_command";
    }

    @Override
    public String getParameter() {
        return "";
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.nitro.description","Quando ativado, entra automaticamente nos quartos de evento sem precisar acessar pelo anúncio.");
    }
}
