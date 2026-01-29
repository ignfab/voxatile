minetest.register_node("darkage:chain", {
	description = "Chain",
	drawtype = "signlike",
	tiles = {"darkage_chain.png"},
	inventory_image = "darkage_chain.png",
	wield_image = "darkage_chain.png",
	paramtype = "light",
	paramtype2 = "wallmounted",
	is_ground_content = false,
	walkable = false,
	climbable = true,
	selection_box = {
		type = "wallmounted",
		--wall_top = = <default>
		--wall_bottom = = <default>
		--wall_side = = <default>
	},
	groups = {snappy=1, cracky=2, oddly_breakable_by_hand=2, not_cuttable=1},
	legacy_wallmounted = true
})


local box_formspec = [[
	size[8,9]
	list[context;main;0,0.3;8,4;]
	list[current_player;main;0,4.85;8,1;]
	list[current_player;main;0,6.08;8,3;8]
	listring[context;main]
	listring[current_player;main]
]].. darkage.formbg

minetest.register_node("darkage:box", {
	description = "Box",
	tiles = { "darkage_box_top.png","darkage_box_top.png","darkage_box.png"},
	groups = { snappy = 3, not_cuttable=1 },
	sounds = default.node_sound_wood_defaults(),
	on_construct = function(pos)
		local meta = minetest.get_meta(pos)
		meta:set_string("formspec", box_formspec )
		meta:set_string("infotext", "Box")
		local inv = meta:get_inventory()
		inv:set_size("main", 16)
	end,
	can_dig = function(pos,player)
		local meta = minetest.get_meta(pos);
		local inv = meta:get_inventory()
		return inv:is_empty("main")
	end,
	on_metadata_inventory_move = function(pos, from_list, from_index, to_list, to_index, count, player)
		minetest.log("action", player:get_player_name()..
				" moves stuff in box at "..minetest.pos_to_string(pos))
	end,
	on_metadata_inventory_put = function(pos, listname, index, stack, player)
		minetest.log("action", player:get_player_name()..
				" moves stuff to box at "..minetest.pos_to_string(pos))
	end,
	on_metadata_inventory_take = function(pos, listname, index, stack, player)
		minetest.log("action", player:get_player_name()..
				" takes stuff from box at "..minetest.pos_to_string(pos))
	end,
})

local shelves_formspec = [[
	size[8,9]
	list[context;up;0,0;8,2;]
	list[context;down;0,2.3;8,2;]

	list[current_player;main;0,4.85;8,1;]
	list[current_player;main;0,6.08;8,3;8]
	listring[context;up]
	listring[context;down]
	listring[current_player;main]
]].. darkage.formbg

minetest.register_node("darkage:wood_shelves", {
	description = "Wooden Shelves",
	tiles = { "darkage_shelves.png","darkage_shelves.png","darkage_shelves.png",
			  "darkage_shelves.png","darkage_shelves.png","darkage_shelves_front.png"},
	paramtype2 = "facedir",
	groups = { snappy = 3, not_cuttable=1 },
	sounds = default.node_sound_wood_defaults(),
	on_construct = function(pos)
		local meta = minetest.get_meta(pos)
		meta:set_string("formspec", shelves_formspec)
		meta:set_string("infotext", "Wooden Shelves")
		local inv = meta:get_inventory()
		inv:set_size("up", 16)
		inv:set_size("down", 16)
	end,
	can_dig = function(pos,player)
		local meta = minetest.get_meta(pos);
		local inv = meta:get_inventory()
		return inv:is_empty("up") and inv:is_empty("down")
	end,
	on_metadata_inventory_move = function(pos, from_list, from_index, to_list, to_index, count, player)
		minetest.log("action", player:get_player_name()..
				" moves stuff in shelves at "..minetest.pos_to_string(pos))
	end,
	on_metadata_inventory_put = function(pos, listname, index, stack, player)
		minetest.log("action", player:get_player_name()..
				" moves stuff to shelves at "..minetest.pos_to_string(pos))
	end,
	on_metadata_inventory_take = function(pos, listname, index, stack, player)
		minetest.log("action", player:get_player_name()..
				" takes stuff from shelves at "..minetest.pos_to_string(pos))
	end,
})

--minetest.register_node("darkage:rosace", {
--	description = "Rose Window",
--	tiles = {"darkage_rosace.png"},
--	is_ground_content = false,
--	groups = {cracky=3},
--	sounds = default.node_sound_stone_defaults()
--})

minetest.register_node("darkage:iron_bars", {
	description = "Iron Bars",
	drawtype = "glasslike",
	tiles = {"darkage_iron_bars.png"},
	inventory_image = "darkage_iron_bars.png",
	wield_image = "darkage_iron_bars.png",
	is_ground_content = false,
	paramtype = "light",
	sunlight_propagates = true,
	groups = {cracky=3 , not_cuttable=1},
	sounds = default.node_sound_stone_defaults()
})

minetest.register_node("darkage:lamp", {
	description = "Lamp",
	tiles = { "darkage_lamp.png" },
	paramtype = "light",
	sunlight_propagates = true,
	light_source = default.LIGHT_MAX-1,
	groups = {snappy=2, cracky=3, oddly_breakable_by_hand=3, flammable=1 , not_cuttable=1},
	sounds = default.node_sound_glass_defaults(),
})

minetest.register_node("darkage:iron_grille", {
	description = "Iron Grille",
	drawtype = "glasslike",
	tiles = {"darkage_iron_grille.png"},
	inventory_image = "darkage_iron_grille.png",
	wield_image = "darkage_iron_grille.png",
	is_ground_content = false,
	paramtype = "light",
	sunlight_propagates = true,
	groups = {cracky=3 , not_cuttable=1},
	sounds = default.node_sound_stone_defaults()
})

minetest.register_node("darkage:wood_bars", {
	description = "Wooden Bars",
	drawtype = "glasslike",
	tiles = {"darkage_wood_bars.png"},
	inventory_image = "darkage_wood_bars.png",
	wield_image = "darkage_wood_bars.png",
	is_ground_content = false,
	paramtype = "light",
	sunlight_propagates = true,
	groups = {snappy=1, choppy=2, not_cuttable=1},
	sounds = default.node_sound_stone_defaults()
})

minetest.register_node("darkage:wood_grille", {
	description = "Wooden Grille",
	drawtype = "glasslike",
	tiles = {"darkage_wood_grille.png"},
	inventory_image = "darkage_wood_grille.png",
	wield_image = "darkage_wood_grille.png",
	is_ground_content = false,
	paramtype = "light",
	sunlight_propagates = true,
	groups = {snappy=1, choppy=2, not_cuttable=1},
	sounds = default.node_sound_stone_defaults()
})

minetest.register_node("darkage:wood_frame", {
	description = "Wooden Frame",
	drawtype = "glasslike_framed",
	tiles = {"darkage_wood_frame.png"},
	inventory_image = "darkage_wood_frame.png",
	wield_image = "darkage_wood_frame.png",
	is_ground_content = false,
	paramtype = "light",
	sunlight_propagates = true,
	groups = {snappy=1, choppy=2, oddly_breakable_by_hand = 3, not_cuttable=1},
	sounds = default.node_sound_stone_defaults()
})

---------------
-- Crafts Item
---------------


----------
-- Crafts 
----------

minetest.register_craft({
	output = "darkage:box",
	recipe = {
		{"default:wood", "", "default:wood"},
		{"",			 "", ""},
		{"default:wood", "", "default:wood"},
	}
})

minetest.register_craft({
	output = "darkage:chain 2",
	recipe = {
		{"darkage:iron_stick"},
		{"darkage:iron_stick"},
		{"darkage:iron_stick"},
	}
})

minetest.register_craft({
	output = "darkage:iron_bars 2",
	recipe = {
		{"darkage:iron_stick", "", "darkage:iron_stick"},
		{"darkage:iron_stick", "", "darkage:iron_stick"},
		{"darkage:iron_stick", "", "darkage:iron_stick"},
	}
})

minetest.register_craft({
	output = "darkage:iron_grille 3",
	recipe = {
    {"",					"darkage:iron_bars",	""},
    {"darkage:iron_bars",	"",						"darkage:iron_bars"},
    {"",					"darkage:iron_bars",	""},
	}
})

minetest.register_craft({
	output = "darkage:lamp",
	recipe = {
		{"group:stick",	"",				 "group:stick"},
		{"",			"default:torch", ""},
		{"group:stick",	"",				 "group:stick"},
	}
})

minetest.register_craft({
	output = "darkage:wood_bars 2",
	recipe = {
    {"group:stick", "", "group:stick"},
    {"group:stick", "", "group:stick"},
    {"group:stick", "", "group:stick"},
	}
})

minetest.register_craft({
	output = "darkage:wood_grille 3",
	recipe = {
		{"",					"darkage:wood_bars", ""},
		{"darkage:wood_bars",	"",					 "darkage:wood_bars"},
		{"",					"darkage:wood_bars", ""},
	}
})

minetest.register_craft({
	output = "darkage:wood_shelves",
	recipe = {
		{"darkage:box"},
		{"darkage:box"},
	}
})

minetest.register_craft({
	output = "darkage:wood_frame",
	recipe = {
		{"group:stick",	"",					"group:stick"},
		{"",			"default:glass",	""},
		{"group:stick",	"",					"group:stick"},
	}
})


-- Cookings
minetest.register_craft({
	type = "cooking",
	output = "default:glass",
	recipe = "darkage:wood_frame",
})


