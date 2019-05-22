# Missing Features
Unfortunately not everything can be migrated, some things are not supported by the Minecraft Bedrock Edition, Nukkit 
or by the tool itself.

Some to address this issue some items and blocks will be replaced by others and some others will be removed from the 
world.

## Unsupported by the tool
These things could be converted but are not supported by the tool yet:
* **Entities**: No entity will be converted, with exception of item frames which are blocks in Bedrock Edition
* **Potion effects**: They will be converted as water potion for now
* **Open maps**: No map content will be migrated yet
* **Firework**: Will not be converted correctly yet
* **Firework stars**: Will not be converted correctly yet
* **Enchantments**: No item will be enchanted after the conversion yet
* **Suspicious Stew**: Will not have the potion effect converted. The behavior after conversion is currently unknown. 

## Unsupported by Minecraft Bedrock Edition
These things have some differences from Java Edition and needs to be treated specially:
* **Item Frames**: They are block in Bedrock Edition, not entities, so they must not overlap any block
to be converted, otherwise it will be skipped.
* **Lever**: Java Edition allows you to place lever facing north, south, east and west when place on the bottom 
or the top part of a block. Bedrock Edition only supports south or east in that condition, so levers pointing to north
will point to south and levers pointing to west will point to east.
* **HideFlags**: The tag is not supported, it will be migrated but it will be ignored by the client
* **Debug Stick and customized states**: The block states in Bedrock Edition are not so customizable as it is in Java,
so customized blocks may have theirs properties reversed, for example if you made a disconnected fence wall, they will
be connected after the conversion
* **Spawn Eggs**: which spawns unsupported entities will be black and won't spawn anything.
* **Custom Player Head**: Player head with skins will loose the skin. The Bedrock Edition doesn't support it.
* **Blocks (+NBT)**: We are unable to pick blocks with it's NBT inside the item, so these blocks will loose the NBT tag.
* **Written Books**: With custom events will loose their events as they are unsupported by Bedrock Edition (click and hover events)

## Unsupported by Nukkit
These things won't work because it's a missing feature or a bug in Nukkit servers:
* **Pistons**: All pistons will loose their head and will not work at all.
* **Dispensers**: They will always face the same direction and will not work.
* **Water-logging**: All water-logged blocks will loose they water on conversion. 
* **Spawn Eggs**: Custom JSON values for entities will not work.
* **Book and Quill**: Writable Books will be converted and will be readable but won't be editable.
* **Attribute Modifiers**: Nukkit doesn't seems to have any support to attribute modifiers.