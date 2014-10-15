package enchantableblocks.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import enchantableblocks.inventory.ContainerChest;

@SideOnly(Side.CLIENT)
public class GuiChest extends GuiContainer
{
    private static final ResourceLocation field_147017_u = new ResourceLocation("textures/gui/container/generic_54.png");
    private IInventory upperChestInventory;
    private IInventory lowerChestInventory;
    public ContainerChest chestContainer;
    public int page = 0;
    public int maxPage = 0;
    public int fullInvoSize;
    /**
     * window height is calculated with these values; the more rows, the heigher
     */
    private int inventoryRows;
    private static final String __OBFID = "CL_00000749";

    public GuiChest(IInventory p_i1083_1_, IInventory p_i1083_2_)
    {
        super(new ContainerChest(p_i1083_1_, p_i1083_2_));
        this.upperChestInventory = p_i1083_1_;
        this.lowerChestInventory = p_i1083_2_;
        this.allowUserInput = false;
        short short1 = 222;
        int i = short1 - 108;
        this.fullInvoSize = p_i1083_2_.getSizeInventory();
        this.inventoryRows = p_i1083_2_.getSizeInventory() / 9 > 6? 6 : p_i1083_2_.getSizeInventory() / 9;
        this.ySize = i + this.inventoryRows * 18;
        maxPage = MathHelper.ceiling_float_int(fullInvoSize/54F) - 1;
        chestContainer = (ContainerChest)this.inventorySlots;
    }
    
    public void initGui()
    {
    	super.initGui();
    	
    	if(maxPage > 0)
    	{
    		if(page > 0)
    		{
    			this.buttonList.add(new GuiButton(0, this.guiLeft - 20, this.guiTop + (this.ySize/2) - 10, 20, 20, "<"));
    		}
    		
    		if(page < maxPage)
    		{
    			this.buttonList.add(new GuiButton(1, this.guiLeft + this.xSize, this.guiTop + (this.ySize/2) - 10, 20, 20, ">"));
    		}
    	}
    }
    
    public void actionPerformed(GuiButton button)
    {
    	int prevPg = page;
    	if(button.id == 0 && page > 0)
    	{
    		page -= 1;
    	} else if(button.id == 1 && page < maxPage)
    	{
    		page += 1;
    	}
    	
    	if(page != prevPg)
    	{
    		this.chestContainer.updatePage(page);
            short short1 = 222;
            int i = short1 - 108;
            this.inventoryRows = this.fullInvoSize / 9 > 6 + (6 * page)? 6 : (this.fullInvoSize / 9 - 1)%6 + 1;
            this.ySize = i + this.inventoryRows * 18;
    		this.buttonList.clear();
    		this.initGui();
    	}
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_)
    {
        this.fontRendererObj.drawString(this.lowerChestInventory.hasCustomInventoryName() ? this.lowerChestInventory.getInventoryName() : I18n.format(this.lowerChestInventory.getInventoryName(), new Object[0]), 8, 6, 4210752);
        this.fontRendererObj.drawString(this.upperChestInventory.hasCustomInventoryName() ? this.upperChestInventory.getInventoryName() : I18n.format(this.upperChestInventory.getInventoryName(), new Object[0]), 8, this.ySize - 96 + 2, 4210752);
    }

    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(field_147017_u);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.inventoryRows * 18 + 17);
        this.drawTexturedModalRect(k, l + this.inventoryRows * 18 + 17, 0, 126, this.xSize, 96);
    }
}