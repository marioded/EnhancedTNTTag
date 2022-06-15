# EnhancedTNTTag
A new multiarena TNTTag plugin for Spigot!

## Building
We use Maven to handle dependencies & building.

### Requirements
To compile the project, you must have: Java 8 JDK, Git and Maven

#### Compiling from source
Run the following command from your command line:
```
git clone https://github.com/zMario34/EnhancedTNTTag.git
cd EnhancedTNTTag
mvn clean install
```

You can find the output plugin JAR in the `target` directory of the `plugin` module.

## Contributing
#### Pull Requests
If you make any changes or improvements to the plugin which you think would be beneficial to others, please consider making a pull request.

#### Project Structure
The project is split up into two separate modules.

* **API** - The API module contains the developer API of the plugin.
* **Plugin** - The plugin module contains the core.
