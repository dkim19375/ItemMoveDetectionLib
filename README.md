![GitHub release (latest by date)](https://img.shields.io/github/v/release/dkim19375/ItemMoveDetectionLib?label=Latest%20Version)
# ItemMoveDetectionLib
A simple library to detect when a player removes or adds an item from their inventory.

Note: For GUI plugins and similar, you should use `InventoryClickEvent` instead of this library.
## How to use
### Getting the dependency (Coming soon... Jitpack is not cooperating 🙁)
Gradle:
```groovy
plugins {
    id 'com.github.johnrengelman.shadow' version '6.1.0'
}

shadowJar {
    // Replace [YOUR PACKAGE] with your base package (ex: me.dkim19375.bedwars)
    relocate 'me.dkim19375.itemmovedetectionlib', '[YOUR PACKAGE].itemmovedetectionlib'
}

repositories {
    maven { url = 'https://jitpack.io' }
}

dependencies {
    // Make sure to replace VERSION with the latest version
    implementation 'com.github.dkim19375:ItemMoveDetectionLib:VERSION' 
}
```
Or with maven:
```xml
<project>
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <version>3.1.1</version>
                <configuration>
                    <relocations>
                        <relocation>
                            <pattern>me.dkim19375.itemmovedetectionlib</pattern>
                            <shadedPattern>[YOUR PACKAGE].itemmovedetectionlib</shadedPattern> <!-- Replace package here here -->
                        </relocation>
                    </relocations>
                </configuration>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>shade</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
    
    <repositories>
        <repository>
            <id>jitpack.io</id>
            <url>https://jitpack.io</url>
        </repository>
    </repositories>
    
    <dependencies>
        <dependency>
            <groupId>com.github.dkim19375</groupId>
            <artifactId>ItemMoveDetectionLib</artifactId>
            <version>VERSION</version> <!-- replace version here -->
        </dependency>
    </dependencies>
</project>
```
### Using the library
You have to register the library's events.

To do that you do `ItemMoveDetectionLib.register()`.

This is a simple plugin to disable stealing items:
```java
package me.dkim19375.coolplugin;

import me.dkim19375.itemmovedetectionlib.event.InventoryItemTransferEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class CoolPlugin extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        ItemMoveDetectionLib.register();
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    private void onTransfer(InventoryItemTransferEvent event) {
        if (event.getType().isOther()) {
            event.getPlayer().sendMessage(ChatColor.RED + "Don't steal!!");
            event.setCancelled(true);
        }
    }
}
```
