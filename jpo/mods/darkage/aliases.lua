--[[
	This function registers alias for all micro, panel, stair nodes that get registered by moreblocks
	Converts all nodes from origin to new
	 * origin: nodename in form of "modname:oldnode"
	 * new: nodename in form of "modname:newnode"
]]
local function register_moreblocks_alias(origin, new)
    local originmod = origin:split(":")[1]
    local originnode = origin:split(":")[2]
    local newmod = new:split(":")[1]
    local newnode = new:split(":")[2]
    local ra = minetest.register_alias
	ra(originmod.. ":micro_" ..originnode.."_1",                      newmod..":micro_" ..newnode.."_1")	
	ra(originmod.. ":panel_" ..originnode.."_1",                      newmod..":panel_" ..newnode.."_1")
	ra(originmod.. ":micro_" ..originnode.."_2",                      newmod..":micro_" ..newnode.."_2")
	ra(originmod.. ":panel_" ..originnode.."_2",                      newmod..":panel_" ..newnode.."_2")
	ra(originmod.. ":micro_" ..originnode.."_4",                      newmod..":micro_" ..newnode.."_4")
	ra(originmod.. ":panel_" ..originnode.."_4",                      newmod..":panel_" ..newnode.."_4")
	ra(originmod.. ":micro_" ..originnode,                            newmod..":micro_" ..newnode)
	ra(originmod.. ":panel_" ..originnode,                            newmod..":panel_" ..newnode)
	ra(originmod.. ":micro_" ..originnode.."_12",                     newmod..":micro_" ..newnode.."_12")
	ra(originmod.. ":panel_" ..originnode.."_12",                     newmod..":panel_" ..newnode.."_12")
	ra(originmod.. ":micro_" ..originnode.."_14",                     newmod..":micro_" ..newnode.."_14")
	ra(originmod.. ":panel_" ..originnode.."_14",                     newmod..":panel_" ..newnode.."_14")
	ra(originmod.. ":micro_" ..originnode.."_15",                     newmod..":micro_" ..newnode.."_15")
	ra(originmod.. ":panel_" ..originnode.."_15",                     newmod..":panel_" ..newnode.."_15")
	
	ra(originmod.. ":stair_" ..originnode.."_outer",                  newmod..":stair_" ..newnode.."_outer")
	ra(originmod.. ":stair_" ..originnode,                            newmod..":stair_" ..newnode)
	ra(originmod.. ":stair_" ..originnode.."_inner",                  newmod..":stair_" ..newnode.."_inner")
	
	ra(originmod.. ":slab_" ..originnode.."_1",                       newmod..":slab_" ..newnode.."_1")
	ra(originmod.. ":slab_" ..originnode.."_2",                       newmod..":slab_" ..newnode.."_2")
	ra(originmod.. ":slab_" ..originnode.."_quarter",                 newmod..":slab_" ..newnode.."_quarter")
	ra(originmod.. ":slab_" ..originnode,                             newmod..":slab_" ..newnode)
	ra(originmod.. ":slab_" ..originnode.."_three_quarter",           newmod..":slab_" ..newnode.."_three_quarter")
	ra(originmod.. ":slab_" ..originnode.."_14",                      newmod..":slab_" ..newnode.."_14")
	ra(originmod.. ":slab_" ..originnode.."_15",                      newmod..":slab_" ..newnode.."_15")
	
	ra(originmod.. ":stair_" ..originnode.."_half",                   newmod..":stair_" ..newnode.."_half")
	ra(originmod.. ":stair_" ..originnode.."_right_half",             newmod..":stair_" ..newnode.."_right_half")
	
	ra(originmod.. ":stair_" ..originnode.."_alt_1",                  newmod..":stair_" ..newnode.."_alt_1")
	ra(originmod.. ":stair_" ..originnode.."_alt_2",                  newmod..":stair_" ..newnode.."_alt_2")
	ra(originmod.. ":stair_" ..originnode.."_alt_4",                  newmod..":stair_" ..newnode.."_alt_4")
	ra(originmod.. ":stair_" ..originnode.."_alt",                    newmod..":stair_" ..newnode.."_alt")
	ra(originmod.. ":slope_" ..originnode,                            newmod..":slope_" ..newnode)
	ra(originmod.. ":slope_" ..originnode.."_half",                   newmod..":slope_" ..newnode.."_half")
	ra(originmod.. ":slope_" ..originnode.."_half_raised",            newmod..":slope_" ..newnode.."_half_raised")
	
	ra(originmod.. ":slope_" ..originnode.."_inner",                  newmod..":slope_" ..newnode.."_inner")
	ra(originmod.. ":slope_" ..originnode.."_inner_half",             newmod..":slope_" ..newnode.."_inner_half")
	ra(originmod.. ":slope_" ..originnode.."_inner_half_raised",      newmod..":slope_" ..newnode.."_inner_half_raised")
	ra(originmod.. ":slope_" ..originnode.."_inner_cut",              newmod..":slope_" ..newnode.."_inner_cut")
	ra(originmod.. ":slope_" ..originnode.."_inner_cut_half",         newmod..":slope_" ..newnode.."_inner_cut_half")
	ra(originmod.. ":slope_" ..originnode.."_inner_cut_half_raised",  newmod..":slope_" ..newnode.."_inner_cut_half_raised")
	
	ra(originmod.. ":slope_" ..originnode.."_outer",                  newmod..":slope_" ..newnode.."_outer")
	ra(originmod.. ":slope_" ..originnode.."_outer_half",             newmod..":slope_" ..newnode.."_outer_half")
	ra(originmod.. ":slope_" ..originnode.."_outer_half_raised",      newmod..":slope_" ..newnode.."_outer_half_raised")
	ra(originmod.. ":slope_" ..originnode.."_outer_cut",              newmod..":slope_" ..newnode.."_outer_cut")
	ra(originmod.. ":slope_" ..originnode.."_outer_cut_half",         newmod..":slope_" ..newnode.."_outer_cut_half")
	ra(originmod.. ":slope_" ..originnode.."_outer_cut_half_raised",  newmod..":slope_" ..newnode.."_outer_cut_half_raised")
	ra(originmod.. ":slope_" ..originnode.."_cut",                    newmod..":slope_" ..newnode.."_cut")
end

-- Ors Brick
minetest.register_alias("darkage:ors_cobble", "darkage:ors_brick")
minetest.register_alias("stairs:slab_ors_cobble", "stairs:slab_ors_brick")
minetest.register_alias("stairs:stair_ors_cobble", "stairs:stair_ors_brick")
register_moreblocks_alias("darkage:ors_cobble", "darkage:ors_brick")
minetest.register_alias("darkage:ors_cobble_wall", "darkage:ors_rubble_wall")

-- Slate Tile
minetest.register_alias(  "darkage:slate_tale", "darkage:slate_tile")
register_moreblocks_alias("darkage:slate_tale", "darkage:slate_tile")


-- Basalt Brick
minetest.register_alias("darkage:basalt_cobble", "darkage:basalt_brick")
register_moreblocks_alias("darkage:basalt_cobble", "darkage:basalt_brick")
minetest.register_alias("darkage:basalt_cobble_wall", "darkage:basalt_rubble_wall")

-- Slate Brick
minetest.register_alias("darkage:slate_cobble", "darkage:slate_brick")
minetest.register_alias("stairs:slab_slate_cobble", "stairs:slab_slate_brick")
minetest.register_alias("stairs:stair_slate_cobble", "stairs:stair_slate_brick")
register_moreblocks_alias("darkage:slate_cobble", "darkage:slate_brick")
minetest.register_alias("darkage:slate_cobble_wall", "darkage:slate_rubble_wall")

-- Gneiss Brick
minetest.register_alias("darkage:gneiss_cobble", "darkage:gneiss_brick")
minetest.register_alias("stairs:slab_gneiss_cobble", "stairs:slab_gneiss_brick")
minetest.register_alias("stairs:stair_gneiss_cobble", "stairs:stair_gneiss_brick")
register_moreblocks_alias("darkage:gneis_cobble", "darkage:gneiss_brick")
minetest.register_alias("darkage:gneiss_cobble_wall", "darkage:gneiss_rubble_wall")

-- Straw
register_moreblocks_alias("darkage:straw", "moreblocks:straw")
