local function get_node_drops(fullRockNode, cobbleRockNode)
	return {
		max_items = 1,
		items = {
			{
				-- drop the cobble variant with 1/3 chance
				items = {cobbleRockNode},
				rarity = 3,
			},
			{
				-- drop the full node with 2/3 chance
				items = {fullRockNode},
			}
		}
	}
end

----------
-- Nodes
----------

minetest.register_node("darkage:adobe", {
	description = "Adobe",
	tiles = {"darkage_adobe.png"},
	is_ground_content = true,
	groups = {crumbly=3},
	sounds = default.node_sound_sand_defaults(),
})

--[[
	Basalt
]]
minetest.register_node("darkage:basalt", {
	description = "Basalt",
	tiles = {"darkage_basalt.png"},
	is_ground_content = true,
	drop = get_node_drops("darkage:basalt","darkage:basalt_rubble"),
	groups = {cracky = 3, stone = 1},
	sounds = default.node_sound_stone_defaults()
})

minetest.register_node("darkage:basalt_rubble", {
	description = "Basalt Rubble",
	tiles = {"darkage_basalt_rubble.png"},
	is_ground_content = false,
	groups = {cracky = 3, stone = 2},
	sounds = default.node_sound_stone_defaults()
})

minetest.register_node("darkage:basalt_brick", {
	description = "Basalt Brick",
	tiles = {"darkage_basalt_brick.png"},
	is_ground_content = false,
	groups = {cracky = 2, stone = 1},
	sounds = default.node_sound_stone_defaults()
})

minetest.register_node("darkage:basalt_block", {
	description = "Basalt Block",
	tiles = {"darkage_basalt_block.png"},
	is_ground_content = false,
	groups = {cracky = 2, stone = 1},
	sounds = default.node_sound_stone_defaults()
})

--[[
	Chalk
]]
minetest.register_node("darkage:chalk", {
	description = "Chalk",
	tiles = {"darkage_chalk.png"},
	is_ground_content = true,
	drop = 'darkage:chalk_powder 2',
	groups = {crumbly=2, cracky=2, not_cuttable=1},
	sounds = default.node_sound_stone_defaults()
})

minetest.register_node("darkage:cobble_with_plaster", {
	description = "Cobblestone with Plaster",
	tiles = {"darkage_chalk.png^(default_cobble.png^[mask:darkage_plaster_mask_D.png)", "darkage_chalk.png^(default_cobble.png^[mask:darkage_plaster_mask_B.png)", 
		"darkage_chalk.png^(default_cobble.png^[mask:darkage_plaster_mask_C.png)", "darkage_chalk.png^(default_cobble.png^[mask:darkage_plaster_mask_A.png)", 
		"default_cobble.png", "darkage_chalk.png"},
	is_ground_content = false,
	paramtype2 = "facedir",
	drop = 'default:cobble',
	groups = {cracky=3, not_cuttable=1},
	sounds = default.node_sound_stone_defaults(),
})

minetest.register_node("darkage:chalked_bricks_with_plaster", {
	description = "Chalked Bricks with Plaster",
	tiles = {"darkage_chalk.png^(darkage_chalked_bricks.png^[mask:darkage_plaster_mask_D.png)", "darkage_chalk.png^(darkage_chalked_bricks.png^[mask:darkage_plaster_mask_B.png)", 
		"darkage_chalk.png^(darkage_chalked_bricks.png^[mask:darkage_plaster_mask_C.png)", "darkage_chalk.png^(darkage_chalked_bricks.png^[mask:darkage_plaster_mask_A.png)", 
		"darkage_chalked_bricks.png", "darkage_chalk.png"},
	is_ground_content = false,
	paramtype2 = "facedir",
	drop = 'default:cobble',
	groups = {cracky=3, not_cuttable=1},
	sounds = default.node_sound_stone_defaults(),
})

--lbm to convert the old cobble_with_plaster to the new chalked_bricks to keep texture consistent
minetest.register_lbm({
	name="darkage:convert_cobble_with_plaster",
	nodenames= "darkage:cobble_with_plaster",
	run_at_every_load = false,
	action = function(pos,node)
		node.name = "darkage:chalked_bricks_with_plaster"
		minetest.swap_node(pos, node)
	end
})

minetest.register_node("darkage:desert_stone_with_iron", {
	description = "Desert Iron Ore",
	tiles = {"default_desert_stone.png^default_mineral_iron.png"},
	is_ground_content = true,
	groups = {cracky=3, not_cuttable=1},
	drop = 'default:iron_lump',
	sounds = default.node_sound_stone_defaults(),
})

minetest.register_node("darkage:darkdirt", {
	description = "Dark Dirt",
	tiles = {"darkage_darkdirt.png"},
	is_ground_content = false,
	groups = {crumbly=2, not_cuttable=1},
	sounds = default.node_sound_dirt_defaults(),
})

minetest.register_node("darkage:dry_leaves", {
	description = "Dry Leaves",
	tiles = {"darkage_dry_leaves.png"},
	is_ground_content = false,
	paramtype = "light",
	groups = {snappy=3, flammable=2},
	sounds = default.node_sound_leaves_defaults()
})

--[[
	Gneiss
]]
minetest.register_node("darkage:gneiss", {
	description = "Gneiss",
	tiles = {"darkage_gneiss.png"},
	is_ground_content = true,
	groups = {cracky = 3, stone = 1},
	drop = get_node_drops("darkage:gneiss", "darkage:gneiss_rubble"),
	sounds = default.node_sound_stone_defaults()
})

minetest.register_node("darkage:gneiss_rubble", {
	description = "Gneiss Rubble",
	tiles = {"darkage_gneiss_rubble.png"},
	is_ground_content = false,
	groups = {cracky = 3, stone = 2},
	sounds = default.node_sound_stone_defaults()
})

minetest.register_node("darkage:gneiss_brick", {
	description = "Gneiss Brick",
	tiles = {"darkage_gneiss_brick.png"},
	is_ground_content = false,
	groups = {cracky = 2, stone = 1},
	sounds = default.node_sound_stone_defaults()
})

minetest.register_node("darkage:gneiss_block", {
	description = "Gneiss Block",
	tiles = {"darkage_gneiss_block.png"},
	is_ground_content = false,
	groups = {cracky = 2, stone = 1},
	sounds = default.node_sound_stone_defaults()
})

--[[
	Marble
]]
minetest.register_node("darkage:marble", {
	description = "Marble",
	tiles = {"darkage_marble.png"},
	is_ground_content = true,
	groups = {cracky = 3, stone = 1},
	sounds = default.node_sound_stone_defaults()
})

minetest.register_node("darkage:marble_tile", {
	description = "Marble Tile",
	tiles = {"darkage_marble_tile.png"},
	is_ground_content = false,
	groups = {cracky=2},
	sounds = default.node_sound_stone_defaults()
})

minetest.register_node("darkage:mud", {
	description = "Mud",
	tiles = {"darkage_mud_up.png","darkage_mud.png"},
	is_ground_content = true,
	groups = {crumbly=3},
	drop = 'darkage:mud_lump 4',
	sounds = default.node_sound_dirt_defaults({
		footstep = "",
	}),
})

--[[
	Old Red Sandstone
]]
minetest.register_node("darkage:ors", {
	description = "Old Red Sandstone",
	tiles = {"darkage_ors.png"},
	is_ground_content = true,
	drop = "darkage:ors_rubble",
	groups = {cracky=2},
	sounds = default.node_sound_stone_defaults()
})

minetest.register_node("darkage:ors_rubble", {
	description = "Old Red Sandstone Rubble",
	tiles = {"darkage_ors_rubble.png"},
	is_ground_content = true,
	groups = {cracky = 3, crumbly=2, stone = 2},
	sounds = default.node_sound_stone_defaults()
})

minetest.register_node("darkage:ors_brick", {
	description = "Old Red Sandstone Brick",
	tiles = {"darkage_ors_brick.png"},
	is_ground_content = false,
	groups = {cracky = 3, stone = 2},
	sounds = default.node_sound_stone_defaults()
})

minetest.register_node("darkage:ors_block", {
	description = "Old Red Sandstone Block",
	tiles = {"darkage_ors_block.png"},
	is_ground_content = false,
	groups = {cracky = 3, stone = 2},
	sounds = default.node_sound_stone_defaults()
})

minetest.register_node("darkage:serpentine", {
	description = "Serpentine",
	tiles = {"darkage_serpentine.png"},
	is_ground_content = true,
	groups = {cracky=3},
	sounds = default.node_sound_stone_defaults()
})

minetest.register_node("darkage:shale", {
	description = "Shale",
	tiles = {"darkage_shale.png","darkage_shale.png","darkage_shale_side.png"},
	is_ground_content = true,
	groups = {crumbly=2,cracky=2},
	sounds = default.node_sound_stone_defaults()
})

minetest.register_node("darkage:schist", {
	description = "Schist",
	tiles = {"darkage_schist.png"},
	is_ground_content = true,
	groups = {cracky=3},
	sounds = default.node_sound_stone_defaults()
})

minetest.register_node("darkage:silt", {
	description = "Silt",
	tiles = {"darkage_silt.png"},
	is_ground_content = true,
	groups = {crumbly=3},
	drop = 'darkage:silt_lump 4',
	sounds = default.node_sound_dirt_defaults({
		footstep = "",
	}),
})

--[[
	Slate
]]
minetest.register_node("darkage:slate", {
	description = "Slate",
	tiles = {"darkage_slate.png","darkage_slate.png","darkage_slate_side.png"},
	is_ground_content = true,
	drop = 'darkage:slate_rubble',
	groups = {cracky=2},
	sounds = default.node_sound_stone_defaults()
})

minetest.register_node("darkage:slate_rubble", {
	description = "Slate Rubble",
	tiles = {"darkage_slate_rubble.png"},
	is_ground_content = false,
	groups = {cracky=2},
	sounds = default.node_sound_stone_defaults()
})

minetest.register_node("darkage:slate_tile", {
	description = "Slate Tile",
	tiles = {"darkage_slate_tile.png"},
	is_ground_content = false,
	groups = {cracky=2},
	sounds = default.node_sound_stone_defaults()
})

minetest.register_node("darkage:slate_block", {
	description = "Slate Block",
	tiles = {"darkage_slate_block.png"},
	is_ground_content = false,
	groups = {cracky=2},
	sounds = default.node_sound_stone_defaults()
})

minetest.register_node("darkage:slate_brick", {
	description = "Slate Brick",
	tiles = {"darkage_slate_brick.png"},
	is_ground_content = false,
	groups = {cracky=2},
	sounds = default.node_sound_stone_defaults()
})

-- Removed straw, because its in minetst game. Registering alias for compatibility reasons
minetest.register_alias("darkage:straw", "farming:straw")

minetest.register_node("darkage:stone_brick", {
	description = "Stone Brick",
	tiles = {"darkage_stone_brick.png"},
	is_ground_content = false,
	groups = {cracky=3},
	sounds = default.node_sound_stone_defaults()
})

minetest.register_node("darkage:straw_bale", {
	description = "Straw Bale",
	tiles = {"darkage_straw_bale.png"},
	is_ground_content = false,
	drop = 'farming:straw 4',
	groups = {snappy=2, flammable=2},
	sounds = default.node_sound_leaves_defaults(),
})

--[[
	Tuff
]]
minetest.register_node("darkage:tuff", {
	description = "Tuff",
	tiles = {"darkage_tuff.png"},
	is_ground_content = true,
	legacy_mineral = true,
	groups = {cracky = 3, stone = 1},
	drop = {
		max_items = 1,
		items = {
			{
				-- player get tuff node if he is lucky :)
				items = {'darkage:tuff'},
				rarity = 3,
			},
			{
				-- player will get rubble with 2/3 chance
				items = {'darkage:tuff_rubble'},
			}

		}
	},
	sounds = default.node_sound_stone_defaults()
})

minetest.register_node("darkage:tuff_bricks", {
	description = "Tuff Bricks",
	tiles = {"darkage_tuff_bricks.png"},
	is_ground_content = false,
	groups = {cracky=2},
	sounds = default.node_sound_stone_defaults()
})

-- abm to turn Tuff bricks to old Tuff bricks if water is nearby
minetest.register_abm({
	nodenames = {"darkage:tuff_bricks"},
	neighbors = {"group:water"},
	interval = 16,
	chance = 200,
	catch_up = false,
	action = function(pos, node)
		minetest.set_node(pos, {name = "darkage:old_tuff_bricks"})
	end
})

minetest.register_node("darkage:tuff_rubble", {
	description = "Tuff Rubble",
	tiles = {"darkage_tuff_rubble.png"},
	groups = {crumbly = 2, falling_node = 1},
	sounds = default.node_sound_gravel_defaults(),
})
--[[
	Rhyolitic Tuff
]]
minetest.register_node("darkage:rhyolitic_tuff", {
	description = "Rhyolitic Tuff",
	tiles = {"darkage_rhyolitic_tuff.png"},
	is_ground_content = true,
	legacy_mineral = true,
	groups = {cracky = 3, stone = 1},
	drop = {
		max_items = 1,
		items = {
			{
				-- player get tuff node if he is lucky :)
				items = {'darkage:rhyolitic_tuff'},
				rarity = 3,
			},
			{
				-- player will get rubble with 2/3 chance
				items = {'darkage:rhyolitic_tuff_rubble'},
			}

		}
	},
	sounds = default.node_sound_stone_defaults()
})



minetest.register_node("darkage:rhyolitic_tuff_bricks", {
	description = "Rhyolitic Tuff Bricks",
	tiles = {"darkage_rhyolitic_tuff_bricks.png"},
	is_ground_content = false,
	groups = {cracky=2},
	sounds = default.node_sound_stone_defaults()
})

minetest.register_node("darkage:old_tuff_bricks", {
	description = "Old Tuff Bricks",
	tiles = {"darkage_old_tuff_bricks.png"},
	is_ground_content = false,
	groups = {cracky=3},
	sounds = default.node_sound_stone_defaults()
})





minetest.register_node("darkage:rhyolitic_tuff_rubble", {
	description = "Rhyolitic Tuff Rubble",
	tiles = {"darkage_rhyolitic_tuff_rubble.png"},
	groups = {crumbly = 2, falling_node = 1},
	sounds = default.node_sound_gravel_defaults(),
})


--[[
	add a node using the cobble texture that was introduced in minetest 0.4.dev-20120408 and got removed in 0.4.7
	It has a nice contrast together the stone bricks, so I think it could get usefull.
]] 
minetest.register_node("darkage:chalked_bricks", {
	description = "Chalked Brick",
	tiles = {"darkage_chalked_bricks.png"},
	is_ground_content = false,
	groups = {cracky = 2, stone = 1},
	sounds = default.node_sound_stone_defaults(),
})

minetest.register_craft({
	output = "darkage:chalked_bricks 4",
	recipe = {
		{"default:stone", 			"default:stone",		"darkage:chalk_powder"},
		{"darkage:chalk_powder",	"darkage:chalk_powder", "darkage:chalk_powder"},
		{"default:stone",			"darkage:chalk_powder", "default:stone"},
	}
})
