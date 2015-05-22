package enchantableblocks.blocks;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import enchantableblocks.blocks.tile.ITileRetention;
import enchantableblocks.blocks.tile.TileEntityEnchantedGeneric;
import enchantableblocks.core.EnchantableBlocks;
import enchantableblocks.enchantments.blocks.BlockEnchantment;

// Any methods created within this class will automatically override ones in the proxied version of 'actualBlock'
public class BlockEnchantedGeneric extends Block implements ITileEntityProvider
{
	public Block original;
	
	public BlockEnchantedGeneric()
	{
		super(Material.wood);
		this.setBlockName("planks");
	}
	
	public BlockEnchantedGeneric(Block block)
	{
		super(block.getMaterial());
		original = block;
		
		this.setBlockName(block.getUnlocalizedName().replaceFirst("tile.", ""));
		this.setHardness((Float)ObfuscationReflectionHelper.getPrivateValue(Block.class, block, "field_149782_v", "blockHardness"));
		this.setLightLevel(block.getLightValue()/15F);
		this.setLightOpacity(block.getLightOpacity());
		this.setBlockTextureName((String)ObfuscationReflectionHelper.getPrivateValue(Block.class, block, "field_149768_d", "textureName"));
		this.setStepSound(block.stepSound);
		
		for(int i = 0; i < 16; i++)
		{
			this.setHarvestLevel(block.getHarvestTool(i), block.getHarvestLevel(i), i);
		}
		
		this.setBlockBounds((float)block.getBlockBoundsMinX(), (float)block.getBlockBoundsMinY(), (float)block.getBlockBoundsMinZ(), (float)block.getBlockBoundsMaxX(), (float)block.getBlockBoundsMaxY(), (float)block.getBlockBoundsMaxZ());
		this.setCreativeTab(EnchantableBlocks.creativeTab);
	}
	
	@Override
	public int getRenderType()
	{
		if(original == null)
		{
			return 0;
		} else
		{
			return original.getRenderType();
		}
	}
	
	@Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess access, int x, int y, int z, int side)
    {
		if(original == null)
		{
			return this.getIcon(side, access.getBlockMetadata(x, y, z));
		} else
		{
			return original.getIcon(access, x, y, z, side);
		}
    }

    /**
     * Gets the block's texture. Args: side, meta
     */
	@Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
		if(original == null)
		{
			return this.blockIcon;
		} else
		{
			return original.getIcon(side, meta);
		}
    }
	
	public Block getBaseBlock(World world, int x, int y, int z)
	{
		Block block = null;
		
		TileEntity tile = world.getTileEntity(x, y, z);
		
		if(tile != null)
		{
			NBTTagCompound tags = new NBTTagCompound();
			tile.writeToNBT(tags);
			
			if(tags.hasKey("block"))
			{
				block = (Block)Block.blockRegistry.getObject(tags.getString("block"));
			}
		}
		
		return block;
	}
	
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
    {
		if(original == null)
		{
			return super.getCollisionBoundingBoxFromPool(world, x, y, z);
		}
		
        float f = 0.125F;
        AxisAlignedBB bounds = original.getCollisionBoundingBoxFromPool(world, x, y, z);
        
        if(bounds == null)
        {
        	return null;
        } else
        {
        	return bounds.contract(f, f, f);
        }
    }

    /**
     * How bright to render this block based on the light its receiving. Args: iBlockAccess, x, y, z
     */
    @SideOnly(Side.CLIENT)
    public int getMixedBrightnessForBlock(IBlockAccess access, int x, int y, int z)
    {
        if(original == null)
        {
        	return super.getMixedBrightnessForBlock(access, x, y, z);
        } else
        {
        	return original.getMixedBrightnessForBlock(access, x, y, z);
        }
    }

    /**
     * Returns true if the given side of this block type should be rendered, if the adjacent block is at the given
     * coordinates.  Args: blockAccess, x, y, z, side
     */
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess access, int x, int y, int z, int side)
    {
    	if(original == null)
    	{
    		return super.shouldSideBeRendered(access, x, y, z, side);
    	} else
    	{
    		return original.shouldSideBeRendered(access, x, y, z, side);
    	}
    }

    /**
     * Returns true if the given side of this block type should be rendered (if it's solid or not), if the adjacent
     * block is at the given coordinates. Args: blockAccess, x, y, z, side
     */
    public boolean isBlockSolid(IBlockAccess access, int x, int y, int z, int side)
    {
    	if(original == null)
    	{
    		return super.isBlockSolid(access, x, y, z, side);
    	} else
    	{
    		return original.isBlockSolid(access, x, y, z, side);
    	}
    }

    /**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    public boolean isOpaqueCube()
    {
    	if(original == null)
    	{
    		return super.isOpaqueCube();
    	} else
    	{
    		return original.isOpaqueCube();
    	}
    }

    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    public boolean renderAsNormalBlock()
    {
    	if(original == null)
    	{
    		return super.renderAsNormalBlock();
    	} else
    	{
    		return original.renderAsNormalBlock();
    	}
    }

    /**
     * checks to see if you can place this block can be placed on that side of a block: BlockLever overrides
     */
    public boolean canPlaceBlockOnSide(World world, int x, int y, int z, int side)
    {
    	if(original == null)
    	{
    		return super.canPlaceBlockOnSide(world, x, y, z, side);
    	} else
    	{
    		return original.canPlaceBlockOnSide(world, x, y, z, side);
    	}
    }

    /**
     * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
     */
    public boolean canPlaceBlockAt(World world, int x, int y, int z)
    {
    	if(original == null)
    	{
    		return super.canPlaceBlockAt(world, x, y, z);
    	} else
    	{
    		return original.canPlaceBlockAt(world, x, y, z);
    	}
    }

    /**
     * Called when a block is placed using its ItemBlock. Args: World, X, Y, Z, side, hitX, hitY, hitZ, block metadata
     */
    public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta)
    {
    	if(original == null)
    	{
    		return super.onBlockPlaced(world, x, y, z, side, hitX, hitY, hitZ, meta);
    	} else
    	{
    		return original.onBlockPlaced(world, x, y, z, side, hitX, hitY, hitZ, meta);
    	}
    }

    /**
     * Returns the bounding box of the wired rectangular prism to render.
     */
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z)
    {
    	if(original == null)
    	{
    		return super.getSelectedBoundingBoxFromPool(world, x, y, z);
    	} else
    	{
    		return original.getSelectedBoundingBoxFromPool(world, x, y, z);
    	}
    }

    /**
     * Updates the blocks bounds based on its current state. Args: world, x, y, z
     */
    public void setBlockBoundsBasedOnState(IBlockAccess access, int x, int y, int z)
    {
    	if(original == null)
    	{
    		super.setBlockBoundsBasedOnState(access, x, y, z);
    	} else
    	{
    		original.setBlockBoundsBasedOnState(access, x, y, z);
    		this.setBlockBounds((float)original.getBlockBoundsMinX(), (float)original.getBlockBoundsMinY(), (float)original.getBlockBoundsMinZ(), (float)original.getBlockBoundsMaxX(), (float)original.getBlockBoundsMaxY(), (float)original.getBlockBoundsMaxZ());
    	}
    }

    /**
     * Sets the block's bounds for rendering it as an item
     */
    public void setBlockBoundsForItemRender()
    {
    	if(original == null)
    	{
    		super.setBlockBoundsForItemRender();
    	} else
    	{
    		original.setBlockBoundsForItemRender();
    		this.setBlockBounds((float)original.getBlockBoundsMinX(), (float)original.getBlockBoundsMinY(), (float)original.getBlockBoundsMinZ(), (float)original.getBlockBoundsMaxX(), (float)original.getBlockBoundsMaxY(), (float)original.getBlockBoundsMaxZ());
    	}
    }
    
    public int getMobilityFlag()
    {
    	if(original == null)
    	{
    		return super.getMobilityFlag();
    	} else
    	{
    		return original.getMobilityFlag();
    	}
    }

    @SideOnly(Side.CLIENT)
    public int getBlockColor()
    {
    	if(original == null)
    	{
    		return super.getBlockColor();
    	} else
    	{
    		return original.getBlockColor();
    	}
    }

    /**
     * Returns the color this block should be rendered. Used by leaves.
     */
    @SideOnly(Side.CLIENT)
    public int getRenderColor(int num)
    {
    	if(original == null)
    	{
    		return super.getRenderColor(num);
    	} else
    	{
    		return original.getRenderColor(num);
    	}
    }

    /**
     * Returns a integer with hex for 0xrrggbb with this color multiplied against the blocks color. Note only called
     * when first determining what to render.
     */
    @SideOnly(Side.CLIENT)
    public int colorMultiplier(IBlockAccess access, int x, int y, int z)
    {
    	if(original == null)
    	{
    		return super.colorMultiplier(access, x, y, z);
    	} else
    	{
    		return original.colorMultiplier(access, x, y, z);
    	}
    }

    /**
     * Returns which pass should this block be rendered on. 0 for solids and 1 for alpha
     */
    @SideOnly(Side.CLIENT)
    public int getRenderBlockPass()
    {
    	if(original == null)
    	{
    		return super.getRenderBlockPass();
    	} else
    	{
    		return original.getRenderBlockPass();
    	}
    }

    /**
     * Called when a user uses the creative pick block button on this block
     *
     * @param target The full target the player is looking at
     * @return A ItemStack to add to the player's inventory, Null if nothing should be added.
     */
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
    {
    	TileEntity tile = world.getTileEntity(x, y, z);
    	int meta = world.getBlockMetadata(x, y, z);
    	
    	ItemStack pickup = new ItemStack(this, 1, meta);
		
		NBTTagCompound tileTags = new NBTTagCompound();
		tile.writeToNBT(tileTags);
		NBTTagList tagList = tileTags.getTagList("ench", 10);
		
		for(int i = 0; i < tagList.tagCount(); i++)
		{
			NBTTagCompound enchTag = tagList.getCompoundTagAt(i);
			short id = enchTag.getShort("id");
			short lvl = enchTag.getShort("lvl");
			
			pickup.addEnchantment(Enchantment.enchantmentsList[id], lvl);
			
			if(id == BlockEnchantment.retention.effectId && tile instanceof ITileRetention)
			{
		        pickup.getTagCompound().setTag("tile", tileTags);
			}
		}
		
		return pickup;
    }

    /**
     * Location sensitive version of getExplosionRestance
     *
     * @param par1Entity The entity that caused the explosion
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z Position
     * @param explosionX Explosion source X Position
     * @param explosionY Explosion source X Position
     * @param explosionZ Explosion source X Position
     * @return The amount of the explosion absorbed.
     */
    public float getExplosionResistance(Entity entity, World world, int x, int y, int z, double explosionX, double explosionY, double explosionZ)
    {
    	if(original == null)
    	{
    		super.getExplosionResistance(entity, world, x, y, z, explosionX, explosionY, explosionZ);
    	}
    	
        float resist = original.getExplosionResistance(entity, world, x, y, z, explosionX, explosionY, explosionZ);
        
        TileEntity tile = world.getTileEntity(x, y, z);
        
        if(tile != null)
        {
    		NBTTagCompound tags = new NBTTagCompound();
    		tile.writeToNBT(tags);
    		
    		if(tags.hasKey("ench") && tags.getTagList("ench", 10).tagCount() > 0)
    		{
    			NBTTagList tagList = tags.getTagList("ench", 10);
    			
    			for(int i = 0; i < tagList.tagCount(); i++)
    			{
    				NBTTagCompound enchTag = tagList.getCompoundTagAt(i);
    				short id = enchTag.getShort("id");
    				short lvl = enchTag.getShort("lvl");
    				
    				if(id == BlockEnchantment.blastResist.effectId)
    				{
    					if(lvl >= BlockEnchantment.blastResist.getMaxLevel())
    					{
    						resist = 2000F;
    					} else
    					{
    						resist += 12F * ((float)lvl/(float)BlockEnchantment.blastResist.getMaxLevel());
    					}
    					break;
    				}
    			}
    		}
        }
        
        return resist;
    }

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    @SuppressWarnings("rawtypes")
	@SideOnly(Side.CLIENT)
    public void getSubBlocks(Item item, CreativeTabs tab, List list)
    {
    	if(original == null)
    	{
    		super.getSubBlocks(item, tab, list);
    	} else
    	{
    		original.getSubBlocks(item, tab, list);
    	}
    }

    /**
     * Chance that fire will spread and consume this block.
     * 300 being a 100% chance, 0, being a 0% chance.
     *
     * @param world The current world
     * @param x The blocks X position
     * @param y The blocks Y position
     * @param z The blocks Z position
     * @param face The face that the fire is coming from
     * @return A number ranging from 0 to 300 relating used to determine if the block will be consumed by fire
     */
    public int getFlammability(IBlockAccess world, int x, int y, int z, ForgeDirection face)
    {
    	if(original == null)
    	{
    		return 0;
    	}
    	
        int chance =  Blocks.fire.getFlammability(original);
        
        TileEntity tile = world.getTileEntity(x, y, z);
        
        if(chance > 0 && tile != null)
        {
    		NBTTagCompound tags = new NBTTagCompound();
    		tile.writeToNBT(tags);
    		
    		if(tags.hasKey("ench") && tags.getTagList("ench", 10).tagCount() > 0)
    		{
    			NBTTagList tagList = tags.getTagList("ench", 10);
    			
    			for(int i = 0; i < tagList.tagCount(); i++)
    			{
    				NBTTagCompound enchTag = tagList.getCompoundTagAt(i);
    				short id = enchTag.getShort("id");
    				
    				if(id == BlockEnchantment.fireProof.effectId)
    				{
    					return 0;
    				}
    			}
    		}
        }
        
        return chance;
    }
	
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune)
    {
    	if(original == null)
    	{
    		return super.getDrops(world, x, y, z, metadata, fortune);
    	}
    	
    	try
    	{
    		return original.getDrops(world, x, y, z, metadata, fortune);
    	} catch(Exception e)
    	{
    		ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
    		drops.add(new ItemStack(this.original, 1, metadata));
    		return drops;
    	}
    }
	
	public void breakBlock(World world, int x, int y, int z, Block block, int meta)
    {
		if(original == null)
		{
			super.breakBlock(world, x, y, z, block, meta);
		}
		
		try
		{
			original.breakBlock(world, x, y, z, block, meta);
		} catch(Exception e)
		{
			super.breakBlock(world, x, y, z, block, meta);
		}
    }
	
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity)
	{
		TileEntity tile = world.getTileEntity(x, y, z);
		
		if(tile != null)
		{
			NBTTagCompound tags = new NBTTagCompound();
    		tile.writeToNBT(tags);
    		
    		if(tags.hasKey("ench") && tags.getTagList("ench", 10).tagCount() > 0)
    		{
    			NBTTagList tagList = tags.getTagList("ench", 10);
    			
    			for(int i = 0; i < tagList.tagCount(); i++)
    			{
    				NBTTagCompound enchTag = tagList.getCompoundTagAt(i);
    				short id = enchTag.getShort("id");
    				short lvl = enchTag.getShort("lvl");
    				
    				Enchantment enchant = Enchantment.enchantmentsList[id];
    				
    				if(enchant instanceof BlockEnchantment)
    				{
    					((BlockEnchantment)enchant).DoEffect(entity, BlockEnchantment.EnchantEventType.TOUCH, lvl, new Object[0]);
    				}
    			}
    		}
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileEntityEnchantedGeneric();
	}
}
