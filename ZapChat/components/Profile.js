import { Image } from "expo-image"
import { StyleSheet, View } from "react-native"
import { Button } from "./Button";
import * as ImagePicker from 'expo-image-picker';
import { useEffect, useState } from "react";

const defStatus = require("../assets/images/pencil.png")

export function Profile({ getFunc, setFunc, icon, text, style }) {

    const [getImage, setImage] = useState(icon)
    const [getFit, setfit] = useState("cover")
    const [getImageSelectBtnText, setImageSelectBtnText] = useState("Select " + text)

    useEffect(() => {
        if (typeof getFunc === 'object' && getFunc.assets && getFunc.assets[0].uri) {

            setImage(getFunc.assets[0].uri);

        } else if (typeof getFunc === 'string') {

            let value = getFunc;

            if (value === "../assets/images/default.svg") {
                setImage(value);
            } else if (value === "../assets/images/pencil.png") {
                setImage(defStatus);
            } else if (value.startsWith("/profile-images/")) {
                const imagePath = process.env.EXPO_PUBLIC_URL + value;
                setImage({ uri: imagePath });
            }

            if (text === "Status Image") {
                setfit("contain");
            }
        } else {
            console.error("Invalid getFunc value:", getFunc);
        }
    }, [getFunc]);

    const pickImage = async () => {

        let result = await ImagePicker.launchImageLibraryAsync({
            mediaTypes: ImagePicker.MediaTypeOptions.All,
            allowsEditing: true,
            aspect: [1, 1],
            quality: 1,
        });

        console.log(result);

        if (!result.canceled) {

            setImageSelectBtnText("Change " + text)
            setFunc(result)
        }
    };

    return (
        <View style={styles.container}>
            <Image
                style={[styles.image, style]}
                source={getImage}
                contentFit={getFit}
                transition={1000}
                contentPosition={"center"}
            />
            <Button text={getImageSelectBtnText} style={styles.design} func={pickImage} />
        </View>
    )
}

const styles = StyleSheet.create({
    design: {
        backgroundColor: "#d1d1d1",
        paddingHorizontal: 10,
        marginTop: 30
    },
    belowView: {
        backgroundColor: "red",
        width: "100%"
    },
    container: {
        alignItems: "center",
        // borderRadius: 100,
        // backgroundColor:"red",
        marginTop: 60
        // justifyContent: "center",
        // alignItems: "center",
        // flex:1
    },
    image: {
        width: 170,
        height: 170,
        borderRadius: 10,
    }
})