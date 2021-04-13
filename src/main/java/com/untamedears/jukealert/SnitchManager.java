package com.untamedears.jukealert;

import com.untamedears.jukealert.model.Snitch;
import com.untamedears.jukealert.model.SnitchQTEntry;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.block.Block;
import vg.civcraft.mc.civmodcore.locations.SparseQuadTree;
import vg.civcraft.mc.civmodcore.locations.chunkmeta.api.SingleBlockAPIView;

public class SnitchManager {

	private SingleBlockAPIView<Snitch> api;
	private Map<UUID, SparseQuadTree<SnitchQTEntry>> quadTreesByWorld;

	public SnitchManager(SingleBlockAPIView<Snitch> api) {
		this.api = api;
		this.quadTreesByWorld = new TreeMap<>();
	}

	public void shutDown() {
		api.disable();
	}

	public Snitch getSnitchAt(Location location) {
		return api.get(location);
	}

	public Snitch getSnitchAt(Block block) {
		return api.get(block.getLocation());
	}

	public void addSnitch(Snitch snitch) {
		api.put(snitch);
		addSnitchToQuadTree(snitch);
	}

	public void addSnitchToQuadTree(Snitch snitch) {
		SparseQuadTree<SnitchQTEntry> quadTree = getQuadTreeFor(snitch.getLocation());
		for (SnitchQTEntry qt : snitch.getFieldManager().getQTEntries()) {
			quadTree.add(qt);
		}
	}

	private SparseQuadTree<SnitchQTEntry> getQuadTreeFor(Location loc) {
		long currentTime = System.currentTimeMillis();
		SparseQuadTree<SnitchQTEntry> tree = quadTreesByWorld.get(loc.getWorld().getUID());
		if (tree == null) {
			JukeAlert.getInstance().getLogger().info("Quad tree for world  " + loc.getWorld().getUID() + " does not exist, creating");
			tree = new SparseQuadTree<>(1);
			quadTreesByWorld.put(loc.getWorld().getUID(), tree);
		}
		JukeAlert.getInstance().info("Time taken on getQuadTreeFor: " + (System.currentTimeMillis() - currentTime) + " ms");
		return tree;
	}

	/**
	 * Removes the given snitch from the QtBox field tracking and the per chunk
	 * block data tracking.
	 * 
	 * Removal from culling timers has to be done outside this call
	 * 
	 * @param snitch Snitch to remove
	 */
	public void removeSnitch(Snitch snitch) {
		api.remove(snitch);
		SparseQuadTree<SnitchQTEntry> quadTree = getQuadTreeFor(snitch.getLocation());
		long currentTime = System.currentTimeMillis();
		for (SnitchQTEntry qt : snitch.getFieldManager().getQTEntries()) {
			quadTree.remove(qt);
		}
		JukeAlert.getInstance().getLogger().info("Time taken to remove quadtree: " + (System.currentTimeMillis() - currentTime) + " ms");
	}

	public Set<Snitch> getSnitchesCovering(Location location) {
		Set <SnitchQTEntry> entries = getQuadTreeFor(location).find(location.getBlockX(), location.getBlockZ(), true);
		Set<Snitch> result = new HashSet<>();
		for(SnitchQTEntry qt : entries) {
			if (qt.getSnitch().getFieldManager().isInside(location)) {
				result.add(qt.getSnitch());
			}
		}
		Iterator<Snitch> iter = result.iterator();
		while (iter.hasNext()) {
			Snitch s = iter.next();
			if (!s.checkPhysicalIntegrity()) {
				iter.remove();
			}
		}
 		return result;
	}

}
