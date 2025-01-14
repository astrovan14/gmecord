# Gmecord

Gmecord is a simple Discord bot/Javalin webserver that connects a
Discord channel and a Groupme chat.

## Features
* The following Groupme attachment types will be parsed to Discord:
	* Images
	* Locations
	* Video URLs

* Mentioning Discord users. (case-sensitive)
* Mentioning Groupme users. (case-sensitive)
* Discord images will be uploaded to Groupme. (If there are multiple
images in a single message, only the first is sent (Groupme bug.))
* If you link to an image in your Discord message, it will be uploaded
and sent to Groupme. (However, if you link to an image and upload an
image to Discord, only the first Discord attachment will be sent.)
* Mentioning @everyone. (Be careful, because for the moment everyone
can use it!
* Mentioning the bridge from Discord will mention the last user to send
a message on the Groupme side.

## NOTE ON GATEWAY INTENTS
By default, the bot will assume you have the server members intent
checked. If not, it will fail to start (for now.) It is required for
mentionables to function.

## Configuration
The configuration of the bot must be in the same directory as the jar
file, and must be called `Config.json`. A file will automatically be
created with all the configuration settings if the file does not exist.
An explanation of configuration is as follows:
* `discordToken`: The token of your Discord bot.
* `groupMeToken`: Your personal Groupme token. This is required to let
Discord mention Groupme users.
* `botID`: The ID of the Groupme bot. Allows for sending messages to
Groupme.
* `channel`: The Discord channel we should listen for messages on.
* `webAuthenticationEnabled`: If you would like basic HTTP
authentication, enable it here. It's recommended you set up a reverse
proxy (nginx or Apache fit this bill fine) and instead do authentication
 there. (You could get HTTPS too.)
* `username`: The username for web authentication.
* `password`: The password for web authentication.
* `botName`: The name of the bot on Groupme, so messages sent from
Discord aren't sent back.
* `groupID`: The ID of the Groupme group. This is needed to let Discord
mention Groupme users.
* `port`: The port the webserver that handles incoming Groupme messages
listens on.

## Docker setup
```
mkdir gmecord-docker
cd gmecord-docker
wget https://raw.githubusercontent.com/astrovan14/gmecord/master/Dockerfile
wget https://raw.githubusercontent.com/astrovan14/gmecord/master/Config.json
nano Config.json #edit the config file and set all the values as described above
docker build -t gmecord .
docker run -d -p 4567:4567 --restart=always -t gmecord
```
After this, set up an https proxy from any domain that you choose, to localhost:4567. Then go to the groupme [bot settings](https://dev.groupme.com/bots) and set the callback URL to `https://yourwebusername:yourwebpassword@subdomain.yourdomain.com/msg/`. `yourwebusername` and `yourwebpassword` should be changed to whatever you put for `username` and `password` in the config file, and `subdomain.yourdomain.com` should be changed to whatever domain you set up the https proxy on.

## Builds
Builds will be periodically posted to this repository's
[Github Releases page.](https://github.com/astrovan14/gmecord/releases)
They will be built for Java 11. Check the parent repository for [old releases](https://github.com/HeyBanditoz/gmecord/releases).
