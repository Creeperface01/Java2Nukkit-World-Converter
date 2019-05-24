package br.com.gamemods.j2nwc

import br.com.gamemods.nbtmanipulator.*

internal fun toNukkitEntity(
    javaEntity: NbtCompound,
    javaChunk: JavaChunk,
    nukkitSections: Map<Int, NukkitChunkSection>,
    nukkitTileEntities: MutableMap<BlockPos, NbtCompound>
): NbtCompound? {
    fun convertBaseEntity(): NbtCompound? {
        return NbtCompound().apply {
            val nukkitId = javaEntities2Nukkit[javaEntity.getNullableString("id")?.removePrefix("minecraft:") ?: return null] ?: return null
            this["id"] = nukkitId
            copyFrom(javaEntity, "CustomName")
            copyFrom(javaEntity, "CustomNameVisible")
            copyFrom(javaEntity, "Pos")
            copyFrom(javaEntity, "Motion")
            copyFrom(javaEntity, "Rotation")
            copyFrom(javaEntity, "FallDistance")
            copyFrom(javaEntity, "Fire")
            copyFrom(javaEntity, "Air")
            copyFrom(javaEntity, "OnGround")
            copyFrom(javaEntity, "Invulnerable")
            copyFrom(javaEntity, "Scale")
            copyFrom(javaEntity, "Silent")
            copyFrom(javaEntity, "NoGravity")
            copyFrom(javaEntity, "Glowing")
            //TODO Scoreboard Tags
            //TODO Passengers
        }
    }
    return when(javaEntity.getString("id").removePrefix("minecraft:")) {
        "painting" -> {
            val nukkitEntity = convertBaseEntity() ?: return null
            val paintingData = paintings[javaEntity.getNullableString("Motive")?.removePrefix("minecraft:") ?: ""] ?: return null
            var (posX, posY, posZ) = nukkitEntity.getDoubleList("Pos").value.map { it.value }
            nukkitEntity.copyFrom(javaEntity, "TileX")
            nukkitEntity.copyFrom(javaEntity, "TileY")
            nukkitEntity.copyFrom(javaEntity, "TileZ")
            val facing = javaEntity.getNullableByte("Facing")
            val direction: Byte = when (facing?.toInt()) {
                0 -> 2
                1 -> 2
                2 -> 2
                3 -> 2
                null -> 2
                else -> 2
            }
            nukkitEntity["Direction"] = direction
            nukkitEntity["Rotation"] = NbtList(NbtFloat(direction.toFloat()), NbtFloat(0F))
            nukkitEntity["Motive"] = paintingData.id
            nukkitEntity
            null //TODO Complete this
        }
        "item_frame" -> {
            val tileX = javaEntity.getInt("TileX")
            val tileY = javaEntity.getInt("TileY")
            val tileZ = javaEntity.getInt("TileZ")
            val chunkSectionY = tileY / 16
            val chunkSection = nukkitSections[chunkSectionY] ?: return null
            val internalX = tileX % 32
            val internalY = tileY % 32
            val internalZ = tileZ % 32
            val offset = ((internalY and 0xF) shl 8) + ((internalZ and 0xF) shl 4) + (internalX and 0xF)
            val blockIdInPosition = chunkSection.blocks[offset].toInt()
            // Item Frame in Bedrock Edition is a block and not an entity, check if we aren't replacing any block.
            if (blockIdInPosition != 0) {
                return null
            }
            val facing = when (javaEntity.getByte("Facing").toInt()) {
                0, 1 -> return null // Facing down and up is unsupported by Bedrock Edition
                2 -> 3
                3 -> 2
                4 -> 5
                5 -> 4
                else -> 3
            }
            chunkSection.blocks[offset] = (199 and 0xFF).toByte()
            val halfOffset = offset / 2
            val isSecond = offset % 2 == 1
            val stored = if (!isSecond) {
                facing to (chunkSection.blockData[halfOffset].toInt() and 0x0F)
            } else {
                ((chunkSection.blockData[halfOffset].toInt() and 0xF0) shr 4) to facing
            }
            val first = stored.first and 0x0F
            val second = (stored.second and 0x0F) shl 4
            val merged = first or second
            chunkSection.blockData[halfOffset] = (merged and 0xFF).toByte()
            val tileEntity = NbtCompound(
                "id" to NbtString("ItemFrame"),
                "x" to NbtInt(tileX),
                "y" to NbtInt(tileY),
                "z" to NbtInt(tileZ),
                "isMoveable" to NbtByte(false)
            )
            tileEntity.copyFrom(javaEntity, "ItemDropChance")
            tileEntity.copyFrom(javaEntity, "ItemRotation")
            javaEntity.getNullableCompound("Item")?.toNukkitItem()?.let {
                tileEntity["Item"] = it
            }
            nukkitTileEntities[BlockPos(tileX, tileY, tileZ)] = tileEntity
            null
        }
        else -> null
    }
}

internal fun NbtCompound.entityToId(): Int? {
    val javaId = getNullableString("id")?.toLowerCase()?.removePrefix("minecraft:") ?: return null
    return java2bedrockEntities[javaId].takeUnless { it == 0 }
}