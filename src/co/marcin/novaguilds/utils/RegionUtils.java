package co.marcin.novaguilds.utils;

import co.marcin.novaguilds.basic.NovaRegion;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class RegionUtils {
	@SuppressWarnings("deprecation")
	public static void setCorner(Player player, Location location, Material material) {
		if(material == null) {
			material = player.getWorld().getBlockAt(location).getType();
		}

		player.sendBlockChange(location,material,(byte) 0);
	}

	@SuppressWarnings("deprecation")
	public static void resetCorner(Player player, Location location) {
		player.sendBlockChange(location,player.getWorld().getBlockAt(location).getType(),(byte) 0);
	}

	@SuppressWarnings("deprecation")
	public static void setCorner(Player player, Location location) {
		player.sendBlockChange(location, Material.EMERALD_BLOCK, (byte) 0);
	}

	public static void resetHighlightRegion(Player player, NovaRegion region) {
		Location loc1 = region.getCorner(0);
		Location loc2 = region.getCorner(1);

		loc1.setY(player.getWorld().getHighestBlockAt(loc1.getBlockX(),loc1.getBlockZ()).getY()-1);
		loc2.setY(player.getWorld().getHighestBlockAt(loc2.getBlockX(),loc2.getBlockZ()).getY()-1);

		setCorner(player, loc1, loc1.getBlock().getType());
		setCorner(player, loc2, loc2.getBlock().getType());
	}

	public static void highlightRegion(Player player, NovaRegion region) {
		Location loc1 = region.getCorner(0);
		Location loc2 = region.getCorner(1);

		loc1.setY(player.getWorld().getHighestBlockAt(loc1.getBlockX(),loc1.getBlockZ()).getY()-1);
		loc2.setY(player.getWorld().getHighestBlockAt(loc2.getBlockX(),loc2.getBlockZ()).getY()-1);

		setCorner(player, loc1, Material.DIAMOND_BLOCK);
		setCorner(player, loc2, Material.DIAMOND_BLOCK);
	}

	//public static int distanceBetweenRegions(NovaRegion region1, NovaRegion region2) {
	public static int distanceBetweenRegions(NovaRegion region1, Location l1, Location l2) {
		int distance = 0;

		int[] x1 = new int[2];
		int[] x2 = new int[2];

		int[] z1 = new int[2];
		int[] z2 = new int[2];

		Location[] c1 = new Location[2];
		Location[] c2 = new Location[2];

		c1[0] = region1.getCorner(0);
		c1[1] = region1.getCorner(1);

//		c2[0] = region2.getCorner(0);
//		c2[1] = region2.getCorner(1);

		c2[0] = l1;
		c2[1] = l2;

		x1[0] = c1[0].getBlockX();
		x1[1] = c1[1].getBlockX();

		x2[0] = c2[0].getBlockX();
		x2[1] = c2[1].getBlockX();

		z1[0] = c1[0].getBlockZ();
		z1[1] = c1[1].getBlockZ();

		z2[0] = c2[0].getBlockZ();
		z2[1] = c2[1].getBlockZ();

		boolean xr1 = x1[0] < x2[0];
		boolean xr2 = x1[1] < x2[1];
		boolean xr3 = x2[0] < x1[1];

		//boolean x_left = (x2[0] < x1[0] && x1[0] < x2[1]);// && !x_between;
		boolean x_left = x2[0] < x1[0] && x2[1] < x1[1] && x1[0] < x2[1];
		//boolean x_right = x1[0] < x2[0] && x1[1] < x2[1] && x2[0] < x1[1];
		boolean x_right = xr1 && xr2 && xr3;
		boolean x_between = (x1[0] < x2[0] && x1[1] > x2[1]) && !(x_left || x_right);
		boolean x_over = x2[0] < x1[0] && x1[1] < x2[1];

		boolean x_inline = x_left || x_right || x_between || x_over;

		Bukkit.getLogger().info("-----------");
		Bukkit.getLogger().info("x_left=" + x_left);
		Bukkit.getLogger().info("x_right=" + x_right + "(" + xr1 +"|"+ xr2 +"|"+ xr3 +")");
		Bukkit.getLogger().info("x_between=" + x_between);
		Bukkit.getLogger().info("x_over=" + x_over);

		Bukkit.getLogger().info("x_inline=" + x_inline);

		return distance;
	}

	public static int distanceBetweenRegions2(NovaRegion region1, Location l1, Location l2) {
		float millis = System.nanoTime();
		double distance = -1;

		int[] x1 = new int[2];
		int[] x2 = new int[2];

		int[] z1 = new int[2];
		int[] z2 = new int[2];

		Location[] c1 = new Location[2];
		Location[] c2 = new Location[2];

		c1[0] = region1.getCorner(0);
		c1[1] = region1.getCorner(1);

//		c2[0] = region2.getCorner(0);
//		c2[1] = region2.getCorner(1);

		c2[0] = l1;
		c2[1] = l2;

		x1[0] = c1[0].getBlockX();
		x1[1] = c1[1].getBlockX();

		x2[0] = c2[0].getBlockX();
		x2[1] = c2[1].getBlockX();

		z1[0] = c1[0].getBlockZ();
		z1[1] = c1[1].getBlockZ();

		z2[0] = c2[0].getBlockZ();
		z2[1] = c2[1].getBlockZ();

		boolean x_out_left = x2[0] < x1[0] && x2[1] < x1[0];
		boolean x_out_right = x1[1] < x2[0] && x1[1] < x2[1];

		boolean x_out = x_out_left || x_out_right;
		Bukkit.getLogger().info("-----");
		Bukkit.getLogger().info("x_out="+x_out);

		boolean z_out_left = z2[0] < z1[0] && z2[1] < z1[0];
		boolean z_out_right = z1[1] < z2[0] && z1[1] < z2[1];

		boolean z_out = z_out_left || z_out_right;
		Bukkit.getLogger().info("z_out="+z_out);

		boolean out = x_out && z_out;
		Bukkit.getLogger().info("out="+out);

		Bukkit.getLogger().info("x1|0="+x1[0]);
		Bukkit.getLogger().info("x1|1="+x1[1]);
		Bukkit.getLogger().info("z1|0="+z1[0]);
		Bukkit.getLogger().info("z1|1="+z1[1]);
		Bukkit.getLogger().info("x2|0="+x2[0]);
		Bukkit.getLogger().info("x2|1="+x2[1]);
		Bukkit.getLogger().info("z2|0="+z2[0]);
		Bukkit.getLogger().info("z2|1="+z2[1]);

		World world = region1.getWorld();
		List<Location> corners1 = new ArrayList<>();
		List<Location> corners2 = new ArrayList<>();

		corners1.add(new Location(world,x1[0],0,x1[0]));
		corners1.add(new Location(world,x1[0],0,x1[1]));
		corners1.add(new Location(world,x1[1],0,x1[1]));
		corners1.add(new Location(world,x1[1],0,x1[0]));

		corners2.add(new Location(world,x2[0],0,x2[0]));
		corners2.add(new Location(world,x2[0],0,x2[1]));
		corners2.add(new Location(world,x2[1],0,x2[1]));
		corners2.add(new Location(world,x2[1],0,x2[0]));

		//corners distances
		if(out) {
			Bukkit.getLogger().info("rectangle1 corners="+corners1.size());
			Bukkit.getLogger().info("rectangle2 corners="+corners2.size());

			for(Location corner1 : corners1) {
				//setCorner(region1.getGuild().getPlayers().get(0).getPlayer(),corner1,Material.BRICK);

				for(Location corner2 : corners2) {
					//Bukkit.getLogger().info("2("+cx2+","+cz2+")");
					//setCorner(region1.getGuild().getPlayers().get(0).getPlayer(),corner2,Material.BRICK);

					double cacheDistance = corner2.distance(corner1);

					if(distance > cacheDistance || distance==-1 ) {
						Bukkit.getLogger().info("Changed distance. "+distance+" -> "+cacheDistance);
						distance = cacheDistance;
					}
				}
			}
		}
		else {
			Bukkit.getLogger().info("side by side!");

			boolean x_inside = x1[0] < x2[0] && x2[1] < x1[1];

			if(x_inside) {
				Bukkit.getLogger().info("x_inside");
				int dif = Math.abs(x1[0] - x2[0]);
				Bukkit.getLogger().info("dif=" + dif);
				distance = dif;

				dif = Math.abs(x1[1] - x2[1]);
				Bukkit.getLogger().info("dif=" + dif);
				if(distance > dif || distance == -1) distance = dif;
			}
			else {
				Bukkit.getLogger().info("z_inside");
				int dif = Math.abs(z1[0] - z2[0]);
				Bukkit.getLogger().info("dif=" + dif);
				distance = dif;

				dif = Math.abs(z1[1] - z2[1]);
				Bukkit.getLogger().info("dif=" + dif);
				if(distance > dif || distance == -1) distance = dif;
			}
		}

		Bukkit.getLogger().info("distance="+Math.round(distance));
		Bukkit.getLogger().info("Time: "+((System.nanoTime()-millis))+"ns");
		return Integer.parseInt(Math.round(distance) + "");
	}

	public static int distanceBetweenRegionsSide(NovaRegion region1, Location l1, Location l2) {
		int distance = -1;

		int[] x1 = new int[2];
		int[] x2 = new int[2];

		int[] z1 = new int[2];
		int[] z2 = new int[2];

		Location[] c1 = new Location[2];
		Location[] c2 = new Location[2];

		c1[0] = region1.getCorner(0);
		c1[1] = region1.getCorner(1);

//		c2[0] = region2.getCorner(0);
//		c2[1] = region2.getCorner(1);

		c2[0] = l1;
		c2[1] = l2;

		x1[0] = c1[0].getBlockX();
		x1[1] = c1[1].getBlockX();

		x2[0] = c2[0].getBlockX();
		x2[1] = c2[1].getBlockX();

		z1[0] = c1[0].getBlockZ();
		z1[1] = c1[1].getBlockZ();

		z2[0] = c2[0].getBlockZ();
		z2[1] = c2[1].getBlockZ();

		boolean x_inside = x1[0] < x2[0] && x2[1] < x1[1];

		if(x_inside) {
			int dif = Math.abs(x1[0] - x2[0]);
			Bukkit.getLogger().info("dif=" + dif);
			distance = dif;

			dif = Math.abs(x1[1] - x2[1]);
			Bukkit.getLogger().info("dif=" + dif);
			if(distance > dif || distance == -1) distance = dif;
		}
		else {
			int dif = Math.abs(z1[0] - z2[0]);
			Bukkit.getLogger().info("dif=" + dif);
			distance = dif;

			dif = Math.abs(z1[1] - z2[1]);
			Bukkit.getLogger().info("dif=" + dif);
			if(distance > dif || distance == -1) distance = dif;
		}

		return distance;
	}
}