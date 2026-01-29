

if minetest.get_modpath("moreblocks") then
	function darkage.register_stairs(nodeName)
        local ndef = assert(minetest.registered_nodes[nodeName], "Error: "..nodeName.." is not registered")

        local mod = "darkage"
        local node = nodeName:split(":")[2]

        local def = table.copy(ndef)
        def.drop = nil

        stairsplus:register_all(mod, node, nodeName, def)
    end
elseif minetest.get_modpath("stairs") then
	function darkage.register_stairs(nodeName)
        local ndef = assert(minetest.registered_nodes[nodeName], "Error: "..nodeName.." is not registered")

        local node = nodeName:split(":")[2]

        -- The stairs api does not allow to use the darkage modname, so we have to call the nodes stairs:stair_darkage_
        -- and creating an alias
        local subname = "darkage_".. node; 
        stairs.register_stair_and_slab(subname, nodeName,
                                       ndef.groups, ndef.tiles, 
                                       ndef.description.." Stair", ndef.description.." Slab",
                                       ndef.sounds)

        --stairs.register_stair_and_slab(subname, recipeitem,
        --                               groups, images, 
        --                               desc_stair, desc_slab, sounds)
        minetest.register_alias("darkage:stair_"..node, "stairs:stair_darkage_"..node)
        minetest.register_alias("darkage:slab_"..node, "stairs:slab_darkage_"..node)
    end
else 
    -- No compatible stairs mod found.
    minetest.log("error", "[darkage] Darkage requires at least moreblocks or stairs to be installed. Its not possible to register stairs.")
    function darkage.register_stairs(nodeName)
        minetest.log("warning", "could not create stair of type "..nodeName .." because no compatible stairs mod is installed.")
    end
end

--[[
if minetest.get_modpath("xdecor") then
    table.insert(workbench.custom_nodes_register, "darkage:straw_bale") -- Straw Bale seems to accidently filtered out
end
]]
-- Uncomment, to check if nodes get registered correctly
--[[
if minetest.get_modpath("xdecor") then
    darkage.isCuttable = {}
    function darkage.register_stairs(nodeName)

        darkage.isCuttable[nodeName] = true;
        
    end
    
end
]]
--place the folowing funktion inside xdecor, to check if everything works fine.
--[[
    then
		if( node:split(":")[1] == "darkage" and not darkage.isCuttable[node]) then
			minetest.log("error", "Error: "..node.. " should not be cuttable")
		end
		nodes[#nodes+1] = node
	else
		if( node:split(":")[1] == "darkage" and darkage.isCuttable[node]) then
			minetest.log("error", "Error: "..node.. " should be cuttable")
		end
]]
