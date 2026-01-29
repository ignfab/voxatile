--[[
	Medival glasses.
	The glasses can be colorized using dye.
	Colorization requires unifieddyes installed.

	Special thanks to Semmett9 for the glass textures.
]]

--[[ Rhombus Glass ]]

minetest.register_node("darkage:glass", {
	description = "Clean Medieval Glass",
	drawtype = "glasslike",
	tiles = {"darkage_glass.png"},
	use_texture_alpha = "clip",
	paramtype = "light",
	sunlight_propagates = true,
	groups = {cracky = 3, oddly_breakable_by_hand = 3, not_cuttable = 1},
	sounds = default.node_sound_glass_defaults(),
})

minetest.register_craft({
	output = "darkage:glass 8",
	recipe = {
		{"default:glass", "default:steel_ingot", "default:glass"},
		{"default:steel_ingot", "default:glass", "default:steel_ingot"},
		{"default:glass", "default:steel_ingot", "default:glass"},
	}
})

minetest.register_node("darkage:glass_round", {
	description = "Round Glass",
	drawtype = "glasslike",
	tiles = {"darkage_glass_round.png"},
	paramtype = "light",
	use_texture_alpha = "clip",
	sunlight_propagates = true,
	sounds = default.node_sound_glass_defaults(),
	groups = {cracky = 3, oddly_breakable_by_hand = 3, not_cuttable = 1},
})

minetest.register_craft({
	output = "darkage:glass_round 8",
	recipe = {
		{"default:steel_ingot", "default:glass", "default:steel_ingot"},
		{"default:glass", "default:glass", "default:glass"},
		{"default:steel_ingot", "default:glass", "default:steel_ingot"},
	}
})

--[[ Square glass By Semmett9 aka Infinatum ]]

minetest.register_node("darkage:glass_square", {
	description = "Square Glass",
	drawtype = "glasslike",
	tiles = {"darkage_glass_square.png"},
	paramtype = "light",
	use_texture_alpha = "clip",
	sunlight_propagates = true,
	sounds = default.node_sound_glass_defaults(),
	groups = {cracky = 3, oddly_breakable_by_hand = 3, not_cuttable = 1},
})

minetest.register_craft({
	output = "darkage:glass_square 8",
	recipe = {
		{"default:glass", "default:steel_ingot", "default:glass"},
		{"default:steel_ingot", "default:steel_ingot", "default:steel_ingot"},
		{"default:glass", "default:steel_ingot", "default:glass"},
	}
})

--[[
		Glowing Glass Variants

		]]

--[[ Rhombus Glow Glass ]]

minetest.register_node("darkage:glow_glass", {
	description = "Medieval Glow Glass",
	drawtype = "glasslike",
	tiles = {"darkage_glass.png"},
	use_texture_alpha = "clip",
	paramtype = "light",
	sunlight_propagates = true,
	light_source = default.LIGHT_MAX - 3,
	groups = {cracky = 3, oddly_breakable_by_hand = 3, not_cuttable = 1},
	sounds = default.node_sound_glass_defaults(),
	inventory_image = minetest.inventorycube("darkage_glow_glass.png")
})

minetest.register_craft({
	output = "darkage:glow_glass 1",
	type = "shaped",
	recipe = {
		{"darkage:glass"},
		{"default:torch"}
	}
})

-- Recycling
minetest.register_craft({
	output = "darkage:glass 1",
	type = "shaped",
	recipe = {{"darkage:glow_glass"}},
})

--[[ Round Glow Glass ]]

minetest.register_node("darkage:glow_glass_round", {
	description = "Medieval Round Glow Glass",
	drawtype = "glasslike",
	tiles = {"darkage_glass_round.png"},
	use_texture_alpha = "clip",
	paramtype = "light",
	sunlight_propagates = true,
	light_source = default.LIGHT_MAX - 3,
	groups = {cracky = 3, oddly_breakable_by_hand = 3, not_cuttable = 1},
	sounds = default.node_sound_glass_defaults(),
	inventory_image = minetest.inventorycube("darkage_glow_glass_round.png")
})

minetest.register_craft({
	output = "darkage:glow_glass_round 1",
	type = "shaped",
	recipe = {
		{"darkage:glass_round"},
		{"default:torch"}
	}
})

-- Recycling
minetest.register_craft({
	output = "darkage:glass_round 1",
	recipe = {{"darkage:glow_glass_round"}}
})

--]] Square Glow Glass ]]

minetest.register_node("darkage:glow_glass_square", {
	description = "Medieval Square Glow Glass",
	drawtype = "glasslike",
	tiles = {"darkage_glass_square.png"},
	use_texture_alpha = "clip",
	paramtype = "light",
	sunlight_propagates = true,
	light_source = default.LIGHT_MAX - 3,
	groups = {cracky = 3, oddly_breakable_by_hand = 3, not_cuttable = 1},
	sounds = default.node_sound_glass_defaults(),
	inventory_image = minetest.inventorycube("darkage_glow_glass_square.png")
})

minetest.register_craft({
	output = "darkage:glow_glass_square 1",
	type = "shaped",
	recipe = {
		{"darkage:glass_square"},
		{"default:torch"},
	}
})

--Recycling
minetest.register_craft({
	output = "darkage:glass_square 1",
	recipe = {{"darkage:glow_glass_square"}}
})

--[[
		Colorizable Milk Glass Variants, depending on unifieddyes mod

		]]

if minetest.get_modpath("unifieddyes") then

	--[[ Rhombus Milk Glass ]]

	minetest.register_node("darkage:milk_glass", {
		description = "Milky Medieval Glass (Good for colorization)",
		drawtype = "glasslike",
		tiles = {"darkage_milk_glass.png"},
		use_texture_alpha = "blend",
		paramtype = "light",
		paramtype2 = "color",
		palette = "unifieddyes_palette_extended.png",
		sunlight_propagates = true,
		groups = {cracky = 3, oddly_breakable_by_hand = 3, not_cuttable = 1, ud_param2_colorable = 1},
		sounds = default.node_sound_glass_defaults()
	})

	unifieddyes.register_color_craft({
		output = "darkage:milk_glass",
		palette = "extended",
		type = "shapeless",
		neutral_node = "darkage:glass",
		recipe = {
			"NEUTRAL_NODE",
			"MAIN_DYE"
		}
	})

	-- Recycling
	minetest.register_craft({
		output = "darkage:glass 1",
		recipe = {{"darkage:milk_glass"}}
	})

	--[[ Round Milk Glass ]]

	minetest.register_node("darkage:milk_glass_round", {
		description = "Milky Medieval Round Glass (Good for colorization)",
		drawtype = "glasslike",
		tiles = {"darkage_milk_glass_round.png"},
		use_texture_alpha = "blend",
		paramtype = "light",
		paramtype2 = "color",
		palette = "unifieddyes_palette_extended.png",
		sunlight_propagates = true,
		groups = {cracky = 3, oddly_breakable_by_hand = 3, not_cuttable = 1, ud_param2_colorable = 1},
		sounds = default.node_sound_glass_defaults()
	})

	unifieddyes.register_color_craft({
		output = "darkage:milk_glass_round",
		palette = "extended",
		type = "shapeless",
		neutral_node = "darkage:glass_round",
		recipe = {
			"NEUTRAL_NODE",
			"MAIN_DYE"
		}
	})

	-- Recycling
	minetest.register_craft({
		output = "darkage:glass_round 1",
		recipe = {{"darkage:milk_glass_round"}}
	})

	--[[ Square Milk Glass ]]

	minetest.register_node("darkage:milk_glass_square", {
		description = "Milky Medieval Square Glass (Good for colorization)",
		drawtype = "glasslike",
		tiles = {"darkage_milk_glass_square.png"},
		use_texture_alpha = "blend",
		paramtype = "light",
		paramtype2 = "color",
		palette = "unifieddyes_palette_extended.png",
		sunlight_propagates = true,
		groups = {cracky = 3, oddly_breakable_by_hand = 3, not_cuttable = 1, ud_param2_colorable = 1},
		sounds = default.node_sound_glass_defaults()
	})

	-- Craft
	minetest.register_craft({
		output = "darkage:milk_glass_square",
		type = "shapeless",
		recipe = {"darkage:glass_square", "dye:white"},
	})

	unifieddyes.register_color_craft({
		output = "darkage:milk_glass_square",
		palette = "extended",
		type = "shapeless",
		neutral_node = "darkage:glass_square",
		recipe = {
			"NEUTRAL_NODE",
			"MAIN_DYE"
		}
	})

	-- Recycling
	minetest.register_craft({
		output = "darkage:glass_square",
		recipe = {{"darkage:milk_glass_square"}}
	})

end --unifieddyes condition
