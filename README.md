# SquareRotation Notes

1. To add a dancer or a couple, press the “Add dancer” button.

  - Some dancers will dance in a couple, and some will dance single.  The “Add dancer” dialog box is designed to handle both cases, and can be used to enter either 1 or 2 dancers.  The dancer(s) entered can be 1 or 2 singles, or 1 couple.

  - Dancers are added individually, regardless of whether they are dancing in a couple.  Do not add “Joe and Nancy” as one dancer.  Add Nancy as Dancer 1 and Joe as Dancer 2, or vice versa; order does not matter.  If the 2 dancers will be dancing together as a couple, check the box labeled “Are Dancer 1 and Dancer 2 dance partners?” to pair the 2 dancers..

  - To enter a single dancer, fill out the “Dancer 1” portion, and press Save.

  - To enter a couple, fill out both the “Dancer 1” and “Dancer 2” portions, and check the box labeled “Are Dancer 1 and Dancer 2 dance partners?”.  Press Save.

  - To enter 2 single dancers, fill out both the “Dancer 1” and “Dancer 2” portions, and do NOT check the box labeled “Are Dancer 1 and Dancer 2 dance partners?”.  Press Save.

  - Dancers who are incorrectly paired with another dancer, or who should be paired with their partner but are not, can easily be paired or un-paired after they have been entered and the “Add dancer” dialog box has been dismissed.

2. Fields on the “Edit Dancer” dialog box

  - Dancer 1 Name
  
    Enter the name of the dancer as it will appear in the display.  IMPORTANT:  because the program pairs single dancers, names must be unique so single dancers can tell who they are paired with.  If there are 2 single Michaels, for example, a display that shows “Michael and Sue” and “Michael and Jane” would be ambiguous.  

    The program will disallow 2 dancers with identical names, so add something like a last initial to the names to make them unique:  “Michael L” and “Michael W”, for example.  If there are 2  Michaels with a last name that begins with “L”, add as many characters as necessary to establish uniqueness.
  
  - Present

    This check box is used to indicate whether a dancer is currently present and available to dance.  The value of “Present” can be changed here, or can also be changed on the main dancer screen.

  - Dancer 1 Outs

    Dancers Outs is used by the program to select dancers for the next tip (dancers with higher Out counts will be selected first when building the squares in a tip).  This field is normally computed by the program and should not be changed, but during program development it was determined that there was a bug computing this field if a square is regenerated.  It was added to this screen so the Out count can be adjusted if the bug reappears, but will likely be removed in future releases as the code stabilizes.

  - Must Dance

    This check box is used to indicate if a Dancer must be selected to dance in the next tip.  It’s important to note, however, that it’s possible to remain out even if this box has been checked.  If, for example, the dancer is single and has chosen to dance a specific role, and there are no available partners to pair with that role (i.e., the dancer is a Belle, and the only single dancers available are also Belles), the unavailability of a partner will override the “Must Dance” setting.

  - Willing to fill in as single if couple is out

    This check box applies to dancers who are coupled with another dancer.  It should be checked if the couple is willing to be split apart and paired with a single if the couple would otherwise not be selected to dance in a tip.  Note that the setting is per dancer, so one dancer in a couple can indicate a willingness to dance with singles, while the other dancer in the couple can choose to remain sitting out if their couple is not selected to dance in a tip.

  - Preferred position as single

    - Select Beau if the dancer will only dance Beau
    - Select Belle if the dancer will only dance Belle
    - Select Either if the dancer is willing to dance either Beau or Belle.

    This field is used to pair single dancers.  If all the single dancers choose a specific role (all “Belle” or all “Beau”), the program will never pair them, and no singles will ever dance.  The more dancers there are who are willing to dance “Either” role, the more chance singles will be selected to dance.  If there is an uneven mix of roles, say 4 Belles and 2 Beaux, then the role in the minority (Beau, in this case) will dance more often than the role in the majority (Belle).

  - Delete 1

    Use this button to delete dancer 1.

  - The fields above are repeated for Dancer 2.

  - Are Dancer 1 and Dancer 2 dance partners?

    Check this check box if Dancer 1 and Dancer 2 will be dancing together as partners.  Leave the box unchecked if Dancer 1 and Dancer 2 are singles.

3. Fields on the Main screen.

  The Main screen is the one with buttons down the left side (Open, Save, Generate New Tip, Regenerate Tip, etc.), and the display of dancers on the right.

  Buttons down the left column of the Main screen are:

  - Open

    The Open button makes it possible to open a previously-saved file of dancers.  Only files that have been saved using the Save button described below should be opened with this button.

  - Save

    The Save button makes it possible to save dancers that have been entered.

  - Generate New Tip

    After all the dancers have been entered, press the Generate New Tip button to generate and display the square and partner assignments for the next tip.

  - Regenerate Tip

    If, after generating a tip, it is discovered that a dancer has changed status (is no longer present, or has returned from being absent, etc.), or a new dancer has arrived, changes can be made (dancer status changed, new dancer(s) entered), and the Regenerate Tip button can be pressed to regenerate the current tip.

    The Regenerate Tip button is different from the Generate New Tip button in that the regeneration process uses the “out counts” from the last tip to determine who should be chosen first for the current tip.  If you use the Generate New Tip button to generate a new tip when the current tip has not actually been danced, the “out counts” will no longer accurately reflect the number of times a dancer has actually been out, which affects dancer rotation.
    
  - Display Current Tip

    The Generate New Tip and Regenerate Tip will automatically cause the Tip screen to display.  If the Tip screen is dismissed, you can press the Display Current Tip button to re-display it without generating or regenerating a new tip.

  - Edit Dancer

    The Edit Dancer button will bring up the Edit Dancer dialog box described in 2 above.  If dancers are present, and one of the dancer rows is highlighted, that dancer will appear in the Dancer 1 portion of the Edit Dancer dialog box.  If that dancer has a partner, the partner will appear in the Dancer 2 portion of the Edit Dancer dialog box.

  - Add Dancer

    The Add Dancer button will bring up a blank version of the Edit Dancer dialog box described in 2 above.  1 or 2 dancers can be added by filling out the dialog box.  If 2 dancers are entered, they can be 2 singles or a couple.

  - Reset to Tip 1

    This button will cause all counts — the current tip count, and all “out” counts — to be reset to their initial values, as if no tips have yet been danced.

  Some column headings on the right hand side of the Main screen can be used to order the display.  If you click on Name, for example, the list will sort alphabetically by Name.  Clicking on Name a second time will reverse the sort.  Clicking on Belle/Beau or Partner will similarly order the display by the chosen column.
    
  Columns down the right of the the Main screen are:

  - Name

    Dancer name.  A double-click on a dancer name will bring up the Edit Dancer dialog box with the dancer in the Dancer 1 area.  If the selected dancer has a partner, the partner will appear in the Dancer 2 area.  Either dancer can then be edited.

  - Beau/Belle

    Indicates whether a dancer dances only Beau, only Belle, or Either.  Used during tip generation to pair singles.

  - Partner

    If this dancer is not a single, this column will contain the dancer’s partner.  If a dancer is single, the column will be blank.

    This column can also be used to pair and un-pair dancers, and to assign new partners:

    - If the column is blank, double-click on it to get a list of dancers who are currently single and can be paired with the current dancer.  Dancers who are currently coupled will not appear in the list, so if every other dancer is paired with another dancer, the list will be empty.  Click on a dancer to make the assignment.  The selected dancer will then appear in this column for the current dancer, and the current dancer will appear in the Partner column of the selected dancer.

    - If the column is not blank, double-click on the column to bring up a list of dancers.  Click on “(No Partner)” to uncouple the current dance partner, making both dancers single.

    - You can also use this column to change partners:  click on the column to bring up a list of dancers, and click on the dancer who should be partnered with the current dancer.  If the selected dancer is different from the current partner, the old partner will become single, and the selected dancer will become the current dancer’s new partner.

  - Present

    If a dancer leaves or wishes to sit out during a tip, click on the Present button to change the dancer’s status to Absent.  If the dancer has a partner, the partner will also be marked Absent.  Dancers marked absent will be excluded from the tip generation process.

    If a dancer is coupled with another dancer, and one dancer wants to keep dancing while the other dancer wants to sit out, the dancers must be uncoupled before their Present/Absent status can be individually adjusted.

  - Must Dance

    If there is some reason a dancer must be included in the next tip, click the Must Dance button.  If the dancer is single and there is no suitable dancer to pair up with, it is possible the dancer will still be out.

  - Willing Single

    This column shows whether a dancer has indicated a willingness to dance as a single if they happen to be in a couple.  See the discussion under “Willing to fill in as single if couple is out” in Section 2.

  - Dancer Outs

    Dancer Outs are calculated by the computer, and track how many times a person has not been selected to dance in a tip.  The count does not increment for people marked Absent during the tip generation process.

4. Tip Display Screen

  This screen is used to display the squares generated for the current tip, listing square numbers and the dancers assigned to them.  Dancers are list in alphabetical order, both in the overall list, and within each couple.  

  Because dancers are listed alphabetically within a couple, the position of single dancers in the overall list can change depending on which dancer they are paired with.  If Jim is paired with Anne, the couple will appear on the page as “Anne & Jim”, and “Anne” will place them towards the beginning of the page.  If Jim is paired with Steve, the couple will appear on this page as “Jim & Steve”, and “Jim” will place them towards the middle of the page.  Because singles can move around the page depending on who they are paired with, their names are highlighted in blue to make them easier to spot.

  If the tip display screen is accidentally dismissed, it can be brought back without generating a new tip by pressing the “Display Current Tip” button on the main screen.

5. Tip Generation Options

  The menu bar at the top of the screen includes an item labeled Tools.  The last menu item in the list under Tools is “Set Options”.  If you select “Set Options”, you are presented with a dialog box with a drop-down list labeled “Singles Handling” that gives 2 choices for handling singles when tips are generated.  

  The 2 choices are:

  - Singles dance only with singles.

    This choice is the default option, and is currently the option that is most thoroughly tested and believed to be working well.  If this choice is selected, couples will always remain couples, and singles will be assigned to dance only with other singles.  Couples will will never be “taken apart” to dance with singles, even if one or both dancers in the couple is marked as “Willing to fill in as single if couple is out”.

  - Singles can dance with Out Couples.

    If this choice is selected, tip generation will take place in two phases.  The first phase will proceed as usual, scanning through the list of available dancers, selecting couples and pairing singles as usual, without “taking apart” any couples.  At the completion of this phase, it is possible that there will be enough “out” dancers to make a square, but only if some couples are split apart (for example:  3 couples, some of whom can dance “Either” and are willing to be split apart if they happen to be out, plus 2 singles who dance “belle” only so cannot be matched to each other.).

    In the second phase, an attempt is made to match the single dancers who are out by “taking apart” couples who are also out to see if it’s possible to make a square.  As development progressed, it became evident that this option only works when there are couples made up of dancers who both dance "Either," and who both are willing to dance single, so it has limited utility.

