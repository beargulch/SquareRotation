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
  
  - Dancing

    This check box is used to indicate whether a dancer is currently present and available to dance.  The value of “Dancgin” can be changed here, or can also be changed on the main dancer screen.

  - Dancer 1 Outs

    Dancers Outs is used by the program to select dancers for the next tip (dancers with higher Out counts will be selected first when building the squares in a tip).  This field is normally computed by the program and should not be changed, but during program development it was determined that there was a bug computing this field if a square is regenerated.  It was added to this screen so the Out count can be adjusted if the bug reappears, but will likely be removed in future releases as the code stabilizes.

  - At the Dance

    There may be times when a dancer has been entered, but is not currently at the dance.  Use this field to indicate if a dancer is not actually present, and is therefore not part of the dance rotation.

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

  Statistics under the buttons down the left column of the Main screen:

  - Current Tip

    The number of the current tip.  This number will increment each time the "Generate New Tip" button is pressed, but will not increment when the "Regenerate Tip" button is pressed.  If the "Reset to Tip 1" button is pressed, the number will be reset to 0.

  - Dancers Present

    The number of dancers who are "At the Dance", regardless of whether they are dancing.

  - Requested Outs

    The number of dancers who are "At the Dance" but are "Out", meaning they are sitting out the tip by request.

  - Dancers Dancing

    The actual number of dancers who are dancing.  This number should always equal "Dancers Present" minus "Requested Outs".

  - Projected Squares

    This is "Dancers Dancing" divided by 8, which means it may not be accurate.  If the number of "Dancers Dancing" is 8, for example, then this number will always be 1, since 8 dancers make a square.  If 6 of those dancers indicate that they only dance belle, however, then the program will be unable to make a square because it will be unable to find partners for every dancer.  This number should therefore be considered an optimistic estimate.

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

  - Dancing

    If a dancer leaves the floor or wishes to sit out during a tip, click on the Dancing button to change the dancer’s status to Out.  If the dancer has a partner, the partner will also be marked Out.  Dancers marked Out will be excluded from the tip generation process.  Whether or not a voluntary Out counts when determining who should dance in the next tip is optional; see section 5, Tip Generation Options.

    If a dancer is coupled with another dancer, and one dancer wants to keep dancing while the other dancer wants to sit out, the dancers must be un-coupled before their Present/Absent status can be individually adjusted.

  - Must Dance

    If there is some reason a dancer must be included in the next tip, click the Must Dance button.  If the dancer is single and there is no suitable dancer to pair up with, it is possible the dancer will still be out.

  - Willing Single

    This column shows whether a dancer has indicated a willingness to dance as a single if they happen to be in a couple.  See the discussion under “Willing to fill in as single if couple is out” in Section 2.

  - Dancer Outs

    Dancer Outs are calculated by the computer, and track how many times a person has not been selected to dance in a tip.  The count does not increment for people marked Absent during the tip generation process.

  - At the Dance

    If a dancer leaves the floor or is not at the dance, he/she can be removed from the rotation by changing clicking on the associated button in the "At the Dance" column to change "Yes" to "No".  Dancers with a value of "No" in this column are excluded from the rotation, and their outs are not counted.

4. Tip Display Screen

  This screen is used to display the squares generated for the current tip, listing square numbers and the dancers assigned to them.  Dancers are list in alphabetical order, both in the overall list, and within each couple.  

  Because dancers are listed alphabetically within a couple, the position of single dancers in the overall list can change depending on which dancer they are paired with.  If Jim is paired with Anne, the couple will appear on the page as “Anne & Jim”, and “Anne” will place them towards the beginning of the page.  If Jim is paired with Steve, the couple will appear on this page as “Jim & Steve”, and “Jim” will place them towards the middle of the page.  Because singles can move around the page depending on who they are paired with, their names are highlighted in blue to make them easier to spot.

  If the tip display screen is accidentally dismissed, it can be brought back without generating a new tip by pressing the “Display Current Tip” button on the main screen.

5. Tip Generation Options

  The menu bar at the top of the screen includes an item labeled Tools.  The last menu item in the list under Tools is “Set Options”.  If you select “Set Options”, you are presented with a dialog box with a drop-down list labeled “Singles Handling” that gives 3 choices for handling singles when tips are generated, plus 2 check boxes.  

  The 3 choices in the drop-down list are:

  - Singles rotate only with Singles.

    This choice is the default option.  If this choice is selected, couples will always remain couples, and singles will be assigned to dance only with other singles.  Couples will will never be “taken apart” to dance with singles, even if one or both dancers in the couple is marked as “Willing to fill in as single if couple is out”.

  - Singles can rotate with Out Couples.

    If this choice is selected, tip generation will take place in two phases.  The first phase will proceed as usual, scanning through the list of available dancers, selecting couples and pairing singles as usual, without “taking apart” any couples.  At the completion of this phase, it is possible that there will be enough “out” dancers to make a square, but only if some couples are split apart.  For example:  3 couples, some of whom can dance “Either” and are willing to be split apart if they happen to be out; plus 2 singles who dance “belle” only so cannot be matched to each other, but could be matched to to dancers in the couples willing to be split apart.

    In the second phase, an attempt is made to match the single dancers who are out by “taking apart” couples who are also out to see if it’s possible to make another square.  This option only works when there are couples made up of dancers who both dance "Either," and who both are willing to dance single, so it is expected to have limited utility.

  - Singles can rotate with Any Couple.

    If this choice is selected, tip generation is based strictly on the number of outs, meaning dancers with the highest number of outs are selected first when building the squares for a tip.  Note that this option only works if there are couples who indicate that they are willing to be "taken apart" to dance single if necessary to keep the single(s) from being out more than other dancers.  If most of the couples are unwilling to be split up, then this option could easily be perceived as unfair by the few couples who are willing to accommodate singles, since they could spend most of their time dancing with someone other than their preferred partner, and some dancers might dance less than dancers in couples who are unwilling to be split apart.
  
  The 2 check boxes are:
  
  - Voluntary out counts as involuntary out.

    As the program generates each tip, it keeps track of how many times a dancer has not been selected to dance, which is counted as an "out."  When a new tip is generated, dancers with the highest out counts are selected first to make sure all dancers are given equal dance time.  If a dancer asks to be taken out of the rotation for a given tip, this option determines whether that out is counted for the purpose of dancer selection for subsequent tips.  If the check box is checked, voluntary outs are treated the same as involuntary outs, meaning they are counted when choosing dancers for subsequent tips (this is the default setting).  If the box is unchecked, voluntary outs will not be counted when choosing dancers.
 
  - Load serialized data (mostly for debugging)

    When the current dance configuration is saved, two files are created:  one with the suffix ".dnc", and one with the suffix ".ser".  The ".dnc" file is the one that is used to capture dancer information for use at another time.  The ".ser" file contains internal program data that is useful for debugging purposes if something goes wrong.  If you discover a bug and wish to report it, it would be a good idea to save the current configuration, then send both the ".dnc" and the ".ser" file to me in an e-mail, along with a description of the bug.  The ".ser" file is useful to help recreate and understand the program behavior that triggered the bug report.  Bug reports should be sent to fred@beargulch.com.