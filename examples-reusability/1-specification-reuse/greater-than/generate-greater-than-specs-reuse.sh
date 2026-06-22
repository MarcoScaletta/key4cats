#!/bin/bash



OUTPUT_FILENAME="specs-reuse.cats"
INPUT_FILE="specs-file-addOne.cats"
TARGET_PROC_REUSED="addOne"

CAT=$(cat $INPUT_FILE)
CAT_WITH_PLACEHOLDER=${CAT//$TARGET_PROC_REUSED/"PROC_TARGET"}

if [ "$#" -eq 0 ]
then
  echo "ERROR: No target procedure was given"
  return
fi



echo "Reusing CAT for  $TARGET_PROC_REUSED  CATs in $OUTPUT_FILENAME"

echo "Creating file $OUTPUT_FILENAME (or resetting if existing)"
echo "" > $OUTPUT_FILENAME
echo "Adding CAT for $TARGET_PROC_REUSED to $OUTPUT_FILENAME"
echo "$CAT" | tee -a $OUTPUT_FILENAME
for var in "$@"
do
    PROC_TARGET="$var"
    NEW_CAT=${CAT_WITH_PLACEHOLDER//"PROC_TARGET"/$PROC_TARGET}
    echo "Generating same CAT for "
    echo "$NEW_CAT" | tee -a $OUTPUT_FILENAME
done

