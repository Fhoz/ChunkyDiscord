# ChunkyDiscord

[<img src="https://github.com/Fhoz/ChunkyDiscord/blob/master/assets/logo.png" width="150"/>](https://github.com/Fhoz/ChunkyDiscord/blob/master/assets/logo.png)

This plugin integrates [Chunky](https://github.com/pop4959/Chunky) with Discord using a Discord bot. With this integration, you can view what Chunky is doing (starting/updating/finishing generation tasks) through Discord, making it easier for you to manage your Minecraft server from whitin Discord.

## Installation

1. Download the latest version of the plugin from the releases page.
1. Install the plugin on your Minecraft server by placing it in the **`plugins`** folder.
1. Restart your server to load the plugin, the plugin will disable itself since it's not configured yet.
1. Create a new Discord bot [**here**](https://discordapp.com/developers/applications/).
1. Invite the bot to your Discord server using the generated link.
1. Copy the bot's token and paste it into the **`bot.token`** field in the plugin's configuration file (located at **`plugins/ChunkyDiscord/config.yml`**). Never share your Discord bot's token with anyone!
1. Copy the channel id from where you want the Discord bot to post messages, and paste it into the **`bot.channel`** field in the plugin's configuration file.
1. Restart your server again to apply the changes.

## Usage

There is nothing you can manage from Discord at the moment. This will come soon. (Starting tasks etc.)

## Configuration

The plugin's configuration file (located at **`plugins/ChunkyDiscord/config.yml`**) contains the following options:

- **`debug`**: Enable/disable debugging messages, you could enable this if you are experiencing issues and writing an issue report. The default value is **`false`**.
- **`update-every-x-ticks`**: How often the bot should update Chunky's task embeds, setting this too low will cause it to not be able to keep up. This interval is multiplied per currently running task (e.g. if you have a task in one world, it's `100`, but if you have a task in two worlds, it will be `200`). The default value is **`100`** ticks.
- **`bot.token`**: The token of the Discord bot.
- **`bot.channel`**: The Discord channel where the Discord bot will post messages related to Chunky.
- **`bot.name`**: The name of the Discord bot. The default value is **`Chunky`**.

Besides the configuration file above, you can change the default profile picture of the bot by placing an image in the **`plugins/ChunkyDiscord/`** directory. Allowed file extensions are `png`, `jpg`, and `jpeg`. (Restart required)

## Troubleshooting

If you are having trouble getting the plugin to work, try the following:

- Make sure that the plugin is installed correctly and that it is compatible with your server's version of Minecraft and the Java version you are running.
- Make sure that the Discord bot is invited to the correct server and that it has the necessary permissions to function.
- Check the plugin's configuration file for any errors or typos.
- Check the server logs for any error messages that might indicate the cause of the problem.

If you are still having trouble, you can try asking for help on the plugin's support Discord server or by opening an issue on this GitHub page.

## Credits

- [Chunky](https://github.com/pop4959/Chunky): The plugin that this integration is based on.
- [JDA](https://github.com/DV8FromTheWorld/JDA): The Discord API wrapper used to connect the plugin to Discord.
