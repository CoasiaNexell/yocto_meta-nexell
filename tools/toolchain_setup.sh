mkdir -p tmp/work
echo -e "\033[40;33m Copying....\033[0m"
echo -e "\033[40;33m     ARM 32bit tool chain copy to tmp/work, It need to be optee build \033[0m"
echo -e "\033[40;33m     please wait...\033[0m"
cp -an ../meta-nexell/tools/toolchain/gcc-linaro-4.9-2014.11-x86_64_arm-linux-gnueabihf tmp/work/ 

echo -e "\033[40;33m     ARM 64bit non-elf tool chain copy to tmp/work, It need to be bl1-artik7 build \033[0m"
echo -e "\033[40;33m     please wait...\033[0m"
cp -an ../meta-nexell/tools/toolchain/gcc-linaro-aarch64-none-elf-4.8-2014.04_linux tmp/work/

echo -e "\033[40;33m     arm-eabi- tool chain copy to tmp/work, It need to be bl1-artik7 build \033[0m"
echo -e "\033[40;33m     please wait...\033[0m"
cp -an ../meta-nexell/tools/toolchain/arm-eabi-4.8 tmp/work/

echo -e "\033[40;33m     arm-cortex_a9-eabi- tool chain copy to tmp/work, for u-boot-avn- build \033[0m"
echo -e "\033[40;33m     please wait...\033[0m"
cp -an ../meta-nexell/tools/toolchain/arm-cortex_a9-eabi-4.7-eglibc-2.18 tmp/work/

echo -e "\033[40;33m Done!\033[0m"
