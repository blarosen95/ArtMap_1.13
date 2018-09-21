package com.github.blarosen95.ArtMap.Config;

import org.bukkit.command.CommandSender;

interface LangSet<T> {
    void send(CommandSender sender);

    T get();
}
