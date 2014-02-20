import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.PriorityQueue;

//////
public class Wolf extends Entity {
	private PriorityQueue<Target> targets = new PriorityQueue<Target>();

	public Wolf(int x, int y, Color c) {
		super(x, y, c);
	}

	public String toString() {
		return "wolf" + super.toString();
	}

	void paint(Graphics pen) {
		pen.setColor(c);
		pen.fillRect(X_MARGIN + xstep * x + 2, Y_MARGIN + ystep * y + 2, 16, 16);
		// draw a line to my target.........
		if (targets.size() != 0 && debugMode) {
			pen.drawLine(X_MARGIN + xstep * x + 4, Y_MARGIN + ystep * y + 4,
					Y_MARGIN + ystep * (targets.peek().getSheep().getX()) + 4,
					Y_MARGIN + ystep * (targets.peek().getSheep().getY()) + 4);
		}
	}

	private void huntAction(ArrayList<Sheep> sheeps, ArrayList<Wolf> wolfs) {
		// new Wolf starts here
		boolean huntAsPack = false;
		while (sheepStillAlive(sheeps)) {

			if (targets.size() != 0) {
				try {
					huntTheTarget(huntAsPack, sheeps, wolfs);

				} catch (NullPointerException e) {
				}
			} else {
				huntAsPack = true;
				if (debugMode) {
					this.c = Color.green;
				}
				// wolf turns green when his Queue ran out............
				createSheepQueue(sheeps);
			}

		}

	}

	private void huntTheTarget(boolean together, ArrayList<Sheep> sheeps,
			ArrayList<Wolf> wolfs) {
		createSheepQueue(sheeps);
		if (!together) {
			anyOneHasThisTarget(wolfs, sheeps);
		}
		makeYourMove();
		anySheepHereKillThem(sheeps);
	}

	private void anyOneHasThisTarget(ArrayList<Wolf> wolfs,
			ArrayList<Sheep> sheeps) {
		for (Wolf wolf : wolfs) {
			try {
				if (!this.equals(wolf)) {
					if (this.targets.peek().getSheep()
							.equals(wolf.targets.peek().getSheep())) {
						okayWhosCloser(wolf, sheeps, wolfs);

					}
				}
			} catch (NullPointerException e) {
			}
		}

	}

	private void okayWhosCloser(Wolf wolf, ArrayList<Sheep> sheeps,
			ArrayList<Wolf> wolfs) {
		// try{
		if (this.targets.peek().getDistance() > wolf.targets.peek()
				.getDistance()) {


			if (targets.size() != 0) {
				targets.remove();
				anyOneHasThisTarget(wolfs, sheeps);
			}
		} else {
			wolf.notMySheep(sheeps);
		}
		// }catch(NoSuchElementException e){}
	}

	public void notMySheep(ArrayList<Sheep> sheeps) {
		this.targets.poll();
		// createSheepQueue(sheeps);
		System.out.println("mine");

	}

	private void anySheepHereKillThem(ArrayList<Sheep> sheeps) {
		for (Sheep sheep : sheeps) {
			if (this.sameCell(sheep)) {
				sheep.die();
			}
		}

	}

	private void makeYourMove() {
		int move = decisionMaker();
		RancherGame.pause();
		this.x = x + choseX(move);
		this.y = y + choseY(move);
	}

	private int decisionMaker() {
		int j = 0;
		for (int q = 1; q < 8; q++) {
			if (getDistance(x + choseX(j) - targets.peek().targetX(), y
					+ choseY(j) - targets.peek().targetY()) > getDistance(x
					+ choseX(q) - targets.peek().targetX(), y + choseY(q)
					- targets.peek().targetY())) {
				j = q;
			}
		}
		return j;
	}

	private double getDistance(int x, int y) {
		return (Math.pow(x, 2) + Math.pow(y, 2));
	}

	private boolean sheepStillAlive(ArrayList<Sheep> sheeps) {
		for (Sheep sheep : sheeps) {
			if (sheep.isAlive()) {
				return true;
			}
		}
		return false;
	}

	private int choseY(int counter) {
		switch (counter) {
		case 0:
			return -1;
		case 1:
			return -1;
		case 2:
			return -1;
		case 3:
			return 0;
		case 4:
			return 0;
		case 5:
			return 1;
		case 6:
			return 1;
		case 7:
			return 1;
		}
		return 0;
	}

	private int choseX(int counter) {
		switch (counter) {
		case 0:
			return -1;
		case 1:
			return 0;
		case 2:
			return 1;
		case 3:
			return -1;
		case 4:
			return 1;
		case 5:
			return -1;
		case 6:
			return 0;
		case 7:
			return 1;
		}
		return 0;
	}

	void hunt(final ArrayList<Sheep> sheeps, final ArrayList<Wolf> wolfs) {

		createSheepQueue(sheeps);

		System.out.println(targets.peek().isTargetStillAlive());

		class MyThread extends Thread {
			public void run() {
				huntAction(sheeps, wolfs);
			}
		}

		MyThread huntThread = new MyThread();
		huntThread.start();
	}

	private void createSheepQueue(ArrayList<Sheep> sheeps) {
		this.targets.clear();
		for (Sheep sheep : sheeps) {
			if (sheep.isAlive()) {
				this.targets.add(new Target(sheep, this));
			}
		}
	}
}