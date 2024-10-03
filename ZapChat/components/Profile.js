import { Image } from "expo-image"
import { StyleSheet, View } from "react-native"
import Ionicons from '@expo/vector-icons/Ionicons';
import { Button } from "./Button";
import { InputField } from "./InputField";
import * as ImagePicker from 'expo-image-picker';

const defaultProfileImage = require("../assets/images/profileDefault.png")

export function Profile({ getFunc,setFunc }) {

    const design = {
        backgroundColor: "#d1d1d1",
        paddingHorizontal: 10,
    }

    const pickImage = async () => {

        let result = await ImagePicker.launchImageLibraryAsync({
            mediaTypes: ImagePicker.MediaTypeOptions.All,
            allowsEditing: true,
            aspect: [4, 3],
            quality: 1,
        });

        console.log(result);

        if (!result.canceled) {
            setFunc(result);
        }
    };

    return (
        <View style={styles.container}>
            <Image
                style={styles.image}
                source={getFunc.assets[0].uri}
                contentFit="cover"
                transition={1000}
                contentPosition={"center"}
            />
            <Button text={"Select Profile Picture"} style={design} func={pickImage} />
        </View>
    )
}

const styles = StyleSheet.create({

    belowView: {
        backgroundColor: "red",
        width: "100%"
    },
    container: {
        alignItems: "center",
        borderRadius: 100,
        justifyContent: "center",
        alignItems: "center",
        width: 170,
        height: 170,
    },
    image: {
        width: "100%",
        height: "100%",
        borderRadius: 100,
    }
})