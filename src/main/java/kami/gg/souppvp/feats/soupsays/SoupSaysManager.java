package kami.gg.souppvp.feats.soupsays;

import kami.gg.souppvp.feats.soupsays.type.*;
import kami.gg.souppvp.util.TaskUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/*
 * Copyright (c) 2026. @Comunidad, made since 23/6/2026
 * Use or redistribution of this source file is only permitted
 * if explicit permission is given by the author.
 */
@Getter @Setter
public class SoupSaysManager {

    private List<Tasks> tasks;
    private Tasks activeTask;

    public SoupSaysManager() {
        this.tasks = List.of(
                new ChatTask(),
                new KillPlayerTask(),
                new TakeFallDamageTask(),
                new DrinkSoupTask(),
                new KillWithSoupTask(),
                new KillWithBowTask(),
                new GetKillstreakTask(),
                new DieTask(),
                new TakeDamageTask(),
                new HealTask()
        );

        startScheduler();
    }

    public Tasks findTask(String id) {
        return this.tasks.stream().filter(it -> it.getTaskID().equalsIgnoreCase(id)).findFirst().orElse(null);
    }

    public void activateRandom() {
        if (this.activeTask != null) {
            this.activeTask.deactivate(null);
        }

        this.activeTask = tasks.get(ThreadLocalRandom.current().nextInt(tasks.size()));
        this.activeTask.activate();
    }

    private void startScheduler() {
        TaskUtil.runTimerAsync(() -> TaskUtil.run(this::activateRandom), 20L * 60 * 10, 20L * 60 * 10);
    }
}
