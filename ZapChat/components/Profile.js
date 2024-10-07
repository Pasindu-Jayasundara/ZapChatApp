import { Image } from "expo-image"
import { StyleSheet, View } from "react-native"
import Ionicons from '@expo/vector-icons/Ionicons';
import { Button } from "./Button";
import { InputField } from "./InputField";
import * as ImagePicker from 'expo-image-picker';
import { useEffect, useState } from "react";
import AsyncStorage from "@react-native-async-storage/async-storage";

const defaultProfileImage = require("../assets/images/profile-page-empty.svg")

export function Profile({ getFunc, setFunc }) {

    const [getImage, setImage] = useState(defaultProfileImage)
    const [getImageSelectBtnText, setImageSelectBtnText] = useState("Select Profile Picture")

    useEffect(() => {

        if (typeof getFunc === 'object') {
            setImage(getFunc.assets[0].uri)
        } else {

            if (getFunc == "../assets/images/default.svg") {
                setImage(getFunc)

            } else if (getFunc.startsWith("/profile-images/")) {

                const imagePath = process.env.EXPO_PUBLIC_URL + getFunc;
                setImage({ uri: imagePath });

            }
        }

    }, [getFunc])

    const pickImage = async () => {

        let result = await ImagePicker.launchImageLibraryAsync({
            mediaTypes: ImagePicker.MediaTypeOptions.All,
            allowsEditing: true,
            aspect: [4, 3],
            quality: 1,
        });

        console.log(result);
        // console.log("getImageResult.assets[0]")
        // console.log(result.assets==null)

        if (!result.canceled) {

            setImageSelectBtnText("Change Profile Picture")
            setFunc(result)
        }
    };

    return (
        <View style={styles.container}>
            <Image
                style={styles.image}
                source={getImage}
                contentFit="cover"
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