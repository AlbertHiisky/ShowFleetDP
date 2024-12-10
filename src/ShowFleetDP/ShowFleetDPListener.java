package ShowFleetDP;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.util.Misc;


public class ShowFleetDPListener extends BaseCampaignEventListener {
    public ShowFleetDPListener() {
        super(false);
    }

    @Override
    public void reportShownInteractionDialog(InteractionDialogAPI dialog) {
        SectorEntityToken target = dialog.getInteractionTarget();

        if (!(target instanceof CampaignFleetAPI)) {
            return;
        }


        CampaignFleetAPI fleet = (CampaignFleetAPI) target;
        PersonAPI commander;

        CampaignFleetAPI combinedFleet = (CampaignFleetAPI) target;
        if (fleet.getBattle() == null) {
            commander = fleet.getCommander();
        }

        else {
            BattleAPI.BattleSide side = fleet.getBattle().pickSide(Global.getSector().getPlayerFleet());
            switch (side) {
                case ONE:
                case TWO:
                    CampaignFleetAPI otherSideCombined = fleet.getBattle().getOtherSideCombined(side);
                    if (otherSideCombined == null) return;
                    commander = otherSideCombined.getCommander();
                    combinedFleet = otherSideCombined;
                    break;
                default:
                    return;
            }
        }

        if (commander == null || commander.isPlayer()) {
            return;
        }

        float strength = combinedFleet.getFleetData().getEffectiveStrength();
        float deploymentPoints = combinedFleet.getFleetData().getFleetPointsUsed();


        for (FleetMemberAPI member : combinedFleet.getFleetData().getMembersListCopy()) {
            deploymentPoints += member.getDeploymentPointsCost();
            strength += Misc.getMemberStrength(member, true, true, true);
        }

        TextPanelAPI textPanel = dialog.getTextPanel();

        textPanel.addPara("The opposing fleet's stats: %s deployment points, %s effective fleet strength.",
                Misc.getTextColor(), Misc.getHighlightColor(), "" + (int)deploymentPoints, "" + (int)strength);
    }
}