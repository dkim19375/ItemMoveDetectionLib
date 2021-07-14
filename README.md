![GitHub release (latest by date)](https://img.shields.io/maven-central/v/io.github.dkim19375/item-move-detection-lib?label=Latest%20Version)
# ItemMoveDetectionLib
A simple library to detect when a player removes or adds an item from their inventory.

Note: For GUI plugins and similar, you should use `InventoryClickEvent` instead of this library.
## How to use
### Getting the dependency 
There are two types: Plugin and shading.
This can be a standalone plugin, or you can choose to include it inside your own plugin jar.

Gradle:
```groovy
plugins {
    id 'com.github.johnrengelman.shadow' version '6.1.0'
}

shadowJar {
    // Replace [YOUR PACKAGE] with your base package (ex: me.dkim19375.bedwars)
    // Do not use if you're not shading, as it will break!!
    relocate 'me.dkim19375.itemmovedetectionlib', '[YOUR PACKAGE].itemmovedetectionlib'
}

dependencies {
    // Make sure to replace VERSION with the latest version
    // Replace "implementation" with "compileOnly" if you're not shading
    implementation 'io.github.dkim19375:item-move-detection-lib:VERSION'
}
```
Or with maven, add the shade plugin: (Do not do this if you're not shading!)
```xml
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
```
Add the dependency: (only use scope provided if you're not shading)
```xml
<dependency>
    <groupId>io.github.dkim19375</groupId>
    <artifactId>item-move-detection-lib</artifactId>
    <version>VERSION</version> <!-- replace version here -->
    
    <scope>provided</scope> <!-- Only include this if you're not shading -->
</dependency>
```

If you're shading, you can skip to the "Using the library" section, if not, 
add this to your `plugin.yml`: 
```yaml
depend: [ ItemMoveDetectionLib ]
```
Or if you want it to be a soft-depend (not required), use this: 
```yaml
softdepend: [ ItemMoveDetectionLib ]
```

### Using the library
You have to register the library's events.

To do that you do `ItemMoveDetectionLib.register()`.

This is a simple plugin to disable stealing items:
```java
package me.dkim19375.coolplugin;

import me.dkim19375.itemmovedetectionlib.ItemMoveDetectionLib;
import me.dkim19375.itemmovedetectionlib.event.InventoryItemTransferEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class CoolPlugin extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        ItemMoveDetectionLib.register(this);
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    private void onTransfer(InventoryItemTransferEvent event) {
        if (event.getType().isFromOther()) {
            event.getPlayer().sendMessage(ChatColor.RED + "Don't steal!!");
            event.setCancelled(true);
        }
    }
}
```