package jadx.core.dex.nodes;

import jadx.core.utils.exceptions.*;

public interface ILoadable {

	/**
	 * On demand loading
	 *
	 * @throws DecodeException
	 */
	void load() throws DecodeException;

	/**
	 * Free resources
	 */
	void unload();

}
